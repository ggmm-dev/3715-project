!function (window, document, undefined) {

	"use strict";

	var
	dateOfCreation       = document.getElementById("created"),
	momentCreated        = moment(dateOfCreation.textContent),
	saveChanges          = document.getElementById("save"),
	rmCollaboratorsBtn   = document.getElementById("rm-c"),
	projectsList         = document.getElementById("projects-list"),
	addCollaboratorBtn   = document.getElementById("add-c"),
	collaboratorList     = document.querySelector("#collaborators > ul"),
	createNewProject     = document.getElementById("new-project"),
	addCollaboratorInput = document.getElementById("add-c-input"),
	description          = document.getElementById("description"),

	updateRightSide = function (data) {
		// the proper UUID
		description.dataset.uuid = data.UUID;
		// header
		$(description).find("h2").text(data.name);
		// date
		momentCreated = moment(data.dateOfCreation, "MMM DD, YYYY h:m:s A");
		dateOfCreation.textContent = "Created " + momentCreated.fromNow();
		// description
		$(description).find("p").text(data.description);
		// peers list
		$(collaboratorList).find("input").parent().remove();
		collaboratorList.innerHTML = data.collaborators.map(function (v, i) {
			return "<li data-uuid=\"" + v.UUID + "\"><input type=\"checkbox\" />" + v.username + "</li>";
		}).join("") + "<li id=\"add-c-input\" contenteditable=\"true\">" + ((data.collaborators.length === 0) ? "Add someone" : "Add someone else") + "</li>";
		// reinit
		initHandlers();
	},
	saveNameDescription = function (e) {
		var newName = document.querySelector("#description > h2").textContent,
		newDesc = document.querySelector("#description > p").textContent,
		uuid = document.querySelector("#description").dataset.uuid,
		updateRequest = JSON.stringify({
			"type": "meta",
			"uuid": uuid,
			"name": newName,
			"description": newDesc
		});
		$.ajax("/api/dataset", {
			type: "PUT",
			data: updateRequest
		}).done(function (data) {
			$(projectsList).find("a[data-uuid=\"" + data.uuid +  "\"]").text(data.name);
			// needs saving
			promptWhenLeaving(false);
		});
	},
	addNewProject = function (e) {
		$.ajax("/api/dataset", {
			type: "POST"
		}).done(function (data) {
			// add a new "managed" dataset
			projectsList.appendChild(function () {
				var li = document.createElement("li"),
				a = document.createElement("a");
				a.dataset.uuid = data.UUID;
				a.textContent = data.name;
				li.appendChild(a);
				li.classList.add("manage");
				console.log(li);
				return li;
			}());
			// reinit
			initHandlers();
			// show the new dataset
			updateRightSide(data);
		});
	},
	showProject = function (e) {
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			// update right side to
			// show the current dataset
			updateRightSide(data);
		});
	},
	addCollaborators = function (e) {
		$.ajax("/api/collaborators", {
			type: "PUT",
			data: JSON.stringify(
				[description.dataset.uuid, addCollaboratorInput.textContent]
			)
		}).done(function (data) {
			if (data === false) {
				return;
			}
			var li = (function () {
				var e = document.createElement("li"),
				checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				e.appendChild(checkbox);
				return e;
			}());
			console.log(li);
			console.log(data.username);
			li.appendChild(document.createTextNode(data.username));
			li.dataset.uuid = data.UUID;
			collaboratorList.insertBefore(li, collaboratorList.firstChild);
			addCollaboratorInput.textContent = "Add someone else";
		});
	},
	rmCollaborators = function (e) {
		var uuid = description.dataset.uuid,
		// find all the checked boxes
		toBeRemovedjQ = $(collaboratorList).find(":checked"),
		// make an array of the parent list elements
		toBeRemoved = $.makeArray(toBeRemovedjQ).map(function (e) {
			return e.parentNode;
		}),
		toBeRemovedUuids = toBeRemoved.map(function (e) {
			return e.dataset.uuid;
		}),
		list = [uuid].concat(toBeRemovedUuids);
		// no users are selected
		if (toBeRemoved.length === 0) {
			return;
		}
		$.ajax("/api/collaborators", {
			type: "DELETE",
			data: JSON.stringify(list)
		}).done(function (data) {
			// delete the nodes
			if (data === true) {
				toBeRemovedjQ.parent().remove();
			}
		});
	},
	promptWhenLeaving = function (b) {
		window.onbeforeunload = (b) ?
		// show a prompt message
		function (msg) {
			saveChanges.style.backgroundColor = "#479e8f";
			return function () {
				return msg;
			};
		}("You should save your changes before leaving.") :
		// returns undefined
		function (a) {
			saveChanges.style.backgroundColor = "white";
			return a;
		}(undefined);
	},
	initHandlers = function () {
		addCollaboratorInput = document.getElementById("add-c-input");
		// better UX when adding a peers email address
		!function () {
			var defaultValue = addCollaboratorInput.textContent;
			addCollaboratorInput.addEventListener("click", function (e) {
				// auto-select all the text in the field
				document.execCommand("selectAll", false, null);
			});
			// if empty restor default value
			addCollaboratorInput.addEventListener("blur", function (e) {
				if (e.target.textContent === "") {
					e.target.textContent = defaultValue;
				}
			});
		}();
		// save the metadata
		saveChanges.addEventListener("click", saveNameDescription);
		// updates the right side to show the proper dataset
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showProject);
		});
		// add a collaborator
		addCollaboratorBtn.addEventListener("click", addCollaborators);
		// remove selected collaborators
		rmCollaboratorsBtn.addEventListener("click", rmCollaborators);
		// edits to the description section could
		// invalidate the entire setup
		description.addEventListener("keyup", function () {
			promptWhenLeaving(true);
		});
	};
	window.addEventListener("DOMContentLoaded", function () {
		// update the date of creation
		// to show time since creation
		!function updateDateOfCreation() {
			// set the cool new date
			dateOfCreation.textContent = "Created " + momentCreated.fromNow();
			// every second
			window.setTimeout(updateDateOfCreation, 60000);
		}();
		// create new project and show it
		createNewProject.addEventListener("click", addNewProject);
		// the dataset is by deafult saved
		promptWhenLeaving(false);
		// init
		initHandlers();
		// if needed, show a intro.js
		if (window.localStorage.getItem("needsIndexIntro") === "n") {
			return;
		}
		// show an introduction
		introJs().start().oncomplete(function () {
			window.localStorage.setItem("needsIndexIntro", "n");
		});
	});

// cache window and document
}(window, window.document);
