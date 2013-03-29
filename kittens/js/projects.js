!function (window, document, undefined) {

	var dateOfCreation = document.getElementById("created");
	var momentCreated = moment(dateOfCreation.innerText);
	window.addEventListener("DOMContentLoaded", function updateDateOfCreation() {
		// set the cool new date
		dateOfCreation.innerText = "Created " + momentCreated.fromNow();
		// every second
		window.setTimeout(updateDateOfCreation, 60000);	
	});

}(window, window.document);
