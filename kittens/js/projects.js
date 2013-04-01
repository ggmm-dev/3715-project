!function (window, document, undefined) {

	"use strict";
	var dateOfCreation = document.getElementById("created"),
	momentCreated = moment(dateOfCreation.textContent),
	createNewProject = document.getElementById("new-project"),
	projectsList = document.getElementById("projects-list"),
	saveChanges = document.getElementById("save"),
	collaboratorList = document.querySelector("#collaborators > ul"),
	addCollaboratorBtn = document.getElementById("add-c"),
	addCollaboratorInput = document.getElementById("add-c-input"),
	rmCollaboratorsBtn = document.getElementById("rm-c"),
	updateRightSide = function (data) {
		var description = document.getElementById("description");
		description.dataset.uuid = data.UUID;
		$(description).find("h2").text(data.name);
		momentCreated = moment(data.dateOfCreation, "MMM DD, YYYY h:m:s A");
		dateOfCreation.textContent = "Created " + momentCreated.fromNow();
		$(description).find("p").text(data.description);
		$(collaboratorList).find("input").parent().remove();
		collaboratorList.innerHTML = data.collaborators.map(function (v, i) {
			return "<li data-uuid=\"" + v.UUID + "\"><input type=\"checkbox\" />" + v.username + "</li>";
		}).join("") + ((data.collaborators.length === 0) ? "<li>Add someone</li>" : "<li>Add someone else</li>");
	},
	saveNameDescription = function (e) {
		console.log(e.target);
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
			console.log(data);
			$(projectsList).find("a[data-uuid=\"" + data.uuid +  "\"]").text(data.name);
		});
	},
	addNewProject = function (e) {
		console.log(e.target);
		$.ajax("/api/dataset", {
			type: "POST"
		}).done(function (data) {
			console.log(data);
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
			initHandlers();
			updateRightSide(data);
		});
	},
	showProject = function (e) {
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			console.log(data);
			updateRightSide(data);
		});
	},
	addCollaborators = function (e) {
		console.log(e.target);
		var uuid =  document.querySelector("#description").dataset.uuid;
		$.ajax("/api/collaborators", {
			type: "PUT",
			data: JSON.stringify([uuid, addCollaboratorInput.textContent])
		}).done(function (data) {
			console.log(data);
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
		console.log(e.target);
		var uuid = document.querySelector("#description").dataset.uuid,
		toBeRemovedjQ = $(collaboratorList).find(":checked"),
		toBeRemoved = $.makeArray(toBeRemovedjQ).map(function (e) {
			return e.parentNode;
		}),
		toBeRemovedUuids = toBeRemoved.map(function (e) {
			return e.dataset.uuid;
		}),
		list = [uuid].concat(toBeRemovedUuids);
		if (toBeRemoved.length == 0) {
			return;
		}
		console.log(list);
		$.ajax("/api/collaborators", {
			type: "DELETE",
			data: JSON.stringify(list)
		}).done(function (data) {
			console.log(data);
			if (data === true) {
				console.log("Yes.");
				toBeRemovedjQ.parent().remove();
			}
		});
	},
	promptWhenLeaving = function (b) {
		window.onbeforeunload = (b) ?
		function () {
			return "You have unsaved changes.";
		} :
		undefined;
		console.log(window.onbeforunload);
	},
	initHandlers = function () {
		saveChanges.addEventListener("click", saveNameDescription);
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showProject);
		});
		addCollaboratorBtn.addEventListener("click", addCollaborators);
		rmCollaboratorsBtn.addEventListener("click", rmCollaborators);
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
		createNewProject.addEventListener("click", addNewProject);
		initHandlers();
	});

}(window, window.document);
