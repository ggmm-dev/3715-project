!function (window, document, undefined) {

	"use strict";
	var dateOfCreation = document.getElementById("created"),
	momentCreated = moment(dateOfCreation.innerText),
	createNewProject = document.getElementById("new-project"),
	projectsList = document.getElementById("projects-list"),
	saveChanges = document.getElementById("save"),
	collaboratorList = document.querySelector("#collaborators > ul"),
	addCollaboratorBtn = document.getElementById("add-c"),
	addCollaboratorInput = document.getElementById("add-c-input"),
	rmCollaboratorsBtn = document.getElementById("rm-c"),
	saveNameDescription = function (e) {
		console.log(e.target);
		var newName = document.querySelector("#description > h2").innerText,
		newDesc = document.querySelector("#description > p").innerText,
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
			var dataset = JSON.parse(data);
			console.log(dataset);
			$(projectsList).find("a[data-uuid=\"" + dataset.uuid +  "\"]").text(dataset.name);
		});
	},
	addNewProject = function (e) {
		console.log(e.target);
		$.ajax("/api/dataset", { type: "POST" }).done(function (data) {
			var dataset = JSON.parse(data);
			console.log(dataset);
			projectsList.appendChild(function () {
				var li = document.createElement("li"),
				a = document.createElement("a");
				a.dataset.uuid = dataset.UUID;
				a.innerText = dataset.name;
				li.appendChild(a);
				console.log(li);
				return li;
			}());
			initHandlers();
		});
	},
	showProject = function (e) {
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			var dataset = JSON.parse(data),
			description = document.getElementById("description");
			console.log(dataset);
			description.dataset.uuid = dataset.UUID;
			$(description).find("h2").text(dataset.name);
			momentCreated = moment(dataset.dateOfCreation, "MMM DD, YYYY h:m:s A");
			dateOfCreation.innerText = "Created " + momentCreated.fromNow();
			$(description).find("p").text(dataset.description);
		});
	},
	addCollaborators = function (e) {
		console.log(e.target);
		var uuid =  document.querySelector("#description").dataset.uuid;
		$.ajax("/api/collaborators", {
			type: "PUT",
			data: JSON.stringify([uuid, addCollaboratorInput.textContent])
		}).done(function (data) {
			var user = JSON.parse(data),
			li = (function () {
				var li = document.createElement("li"),
				checkbox = document.createElement("input");
				checkbox.type = "checkbox";
				li.appendChild(checkbox);
				return li;
			}());
			console.log(li);
			console.log(user);
			if (user === false) {
			}
			else {
				console.log(user.username);
				li.appendChild(document.createTextNode(user.username));
				li.dataset.uuid = user.UUID;
				collaboratorList.insertBefore(li, collaboratorList.firstChild);
				addCollaboratorInput.textContent = "Add someone else";
			}
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
			var success = JSON.parse(data);
			console.log(success);
			if (success === true) {
				console.log("Yes.");
				toBeRemovedjQ.parent().remove();
			}
		});
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
			dateOfCreation.innerText = "Created " + momentCreated.fromNow();
			// every second
			window.setTimeout(updateDateOfCreation, 60000);
		}();
		createNewProject.addEventListener("click", addNewProject);
		initHandlers();
	});

}(window, window.document);
