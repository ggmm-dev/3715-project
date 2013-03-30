!function (window, document, undefined) {

	"use strict";
	var dateOfCreation = document.getElementById("created"),
	momentCreated = moment(dateOfCreation.innerText),
	createNewProject = document.getElementById("new-project"),
	projectsList = document.getElementById("projects-list"),
	saveChanges = document.getElementById("save"),
	collaboratorList = document.querySelector("#collaborators > ul"),
	addCollaborator = document.getElementById("add-c"),
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
			$(projectsList).find("a[data-uuid=\"" + dataset.uuid +  "\"]").text(dataset.name);
		});
	},
	addNewProject = function (e) {
		console.log(e.target);
		$.ajax("/api/dataset", { type: "POST" }).done(function (data) {
			var dataset = JSON.parse(data);
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
			description = document.getElementById("description"),
			collaborators = document.getElementById("collaborators");
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
	},
	initHandlers = function () {
		saveChanges.addEventListener("click", saveNameDescription);
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showProject);
		});
		addCollaborator.addEventListener("click", addCollaborators);
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
