!function (window, document, undefined) {

	var dateOfCreation = document.getElementById("created"),
	    momentCreated = moment(dateOfCreation.innerText),
	    createNewProject = document.getElementById("new-project");
	window.addEventListener("DOMContentLoaded", function () {
		!function updateDateOfCreation() {
			// set the cool new date
			dateOfCreation.innerText = "Created " + momentCreated.fromNow();
			// every second
			window.setTimeout(updateDateOfCreation, 60000);
		}();
		createNewProject.addEventListener("click", function () {
			var dataset = new Dataset("Another sample", "Another sample dataset.");
			dataset.setHeaders([ "Apples", "Oranges" ]).addRows([ "First", "Second" ]);
			$.ajax("/api/dataset", {
				type: "PUT",
				data: dataset.toString()
			});
		});
	});

}(window, window.document);
