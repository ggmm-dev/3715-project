!function (window, document, undefined) {

	"use strict";

	// this an array of unique values
	// O(n^2)
	Array.prototype.unique = function () {
		var r = [],
		i, n, j, m;
		o:
		for (i = 0, n = this.length; i < n; i++) {
			for (j = 0, m = r.length; j < m; j++) {
				if (r[j] === this[i]) {
					continue o;
				}
			}
			r[r.length] = this[i];
		}
		return r.length === this.length;
	};

	var i = 0,
	dataDiv          = document.getElementById("data"),
	addRowBtn        = document.getElementById("add-row"),
	addColBtn        = document.getElementById("add-col"),
	createNewProject = document.getElementById("new-project"),
	projectsList     = document.getElementById("projects-list"),
	saveChangesBtn   = document.getElementById("save"),
	dropZone         = document.getElementById("drop-zone"),
	dataTable        = document.getElementById("data-table"),
	downloadBtn      = document.getElementById("download"),
	rmColBtn         = document.getElementById("rm-col"),
	rmRowBtn         = document.getElementById("rm-row"),

	updateRightSide = function (data) {
		var head = document.querySelector("#data-table thead tr"),
		tbody = document.querySelector("#data-table tbody"),
		j = 0;
		// set the headers
		head.innerHTML = data.headers.map(function (v) {
			return "<th><h6 contenteditable=\"true\">" + v + "</h6></th>";
		}).join("");
		// set the table inners
		tbody.innerHTML = data.rows.map(function (r) {
			return "<tr>" + r.values.map(function (v) {
				return "<td contenteditable=\"true\">" + v + "</td>";
			}).join("") + "</tr>";
		}).join("");
		// set the name
		$("#data > h2").text(data.name);
		// the right UUID
		dataDiv.dataset.uuid = data.UUID;
		// the right download URL
		downloadBtn.href = "/download/dataset?uuid=" + data.UUID;
	},
	addRow = function (e) {
		// add a new row
		var newRow = dataTable.insertRow(-1),
		j = 0,
		cell;
		// fill it with columns
		for (j = 0; j < i; j++) {
			cell = newRow.insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode("fill me"));
		}
		// needs to be saved
		promptWhenLeaving(true);
	},
	rmRow = function (e) {
		// min row count
		if (dataTable.rows.length <= 2) {
			return;
		}
		dataTable.deleteRow(-1);
		// needs saving
		promptWhenLeaving(true);
	},
	addCol = function (e) {
		var rowCount = dataTable.rows.length,
		cell,
		h6,
		th,
		j = 0;
		for (j = 0; j < rowCount; j++) {
			if (j === 0) {
				// the table header
				th = document.createElement("th");
				h6 = document.createElement("h6");
				h6.contentEditable = true;
				h6.textContent = "New Column";
				th.appendChild(h6);
				dataTable.rows[j].appendChild(th);
				continue;
			}
			cell = dataTable.rows[j].insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode("fill me"));
		}
		// new col count
		i = $("#data-table th").size();
		// changes need to be saved
		promptWhenLeaving(true);
	},
	rmCol = function (e) {
		var rowCount = dataTable.rows.length,
		colCount = dataTable.rows[0].cells.length,
		j = 0;
		for (j = 0; j < rowCount; j++) {
			// min col count
			if (colCount <= 1) {
				return;
			}
			dataTable.rows[j].deleteCell(-1);
		}
		// needs to be saved
		promptWhenLeaving(true);
	},
	saveChanges = function (e) {
		var uuid = dataDiv.dataset.uuid,
		headers = [],
		rows = [];
		// the headers
		$("#data-table thead h6").each(function () {
			headers.push(this.textContent);
		});
		// the data
		$("#data-table tbody tr").each(function () {
			var cells = [];
			$(this).find("td").each(function () {
				cells.push(this.textContent);
			});
			rows.push(cells);
		});
		// database columns need to be unique
		if (!headers.unique()) {
			window.alert("Column headers need to be unique.");
			return;
		}
		var dataset = JSON.stringify({
			"uuid": uuid,
			"headers": headers,
			"rows": rows,
			"type": "data"
		});
		$.ajax("/api/dataset", {
			type: "PUT",
			data: dataset
		}).done(function (data) {
			// don't prompt when leaving
			promptWhenLeaving(false);
		});
	},
	showProject = function (e) {
		console.log(e.target);
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			// show the dataset
			updateRightSide(data);
		});
	},
	addNewProject = function (e) {
		$.ajax("/api/dataset", {
			type: "POST"
		}).done(function (data) {
			// adds a new "managed" dataset
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
			// reinit handlers
			initHandlers();
			// show the new project
			updateRightSide(data);
		});
	},
	initHandlers = function () {
		// adds new projects
		createNewProject.addEventListener("click", addNewProject);
		// adds rows
		addRowBtn.addEventListener("click", addRow);
		// removes columns
		rmRowBtn.addEventListener("click", rmRow);
		// adds columns
		addColBtn.addEventListener("click", addCol);
		// removes columns
		rmColBtn.addEventListener("click", rmCol);
		// saves changes
		saveChangesBtn.addEventListener("click", saveChanges);
		// updates the right side of the view
		// to display the proper dataset
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showProject);
		});
	},
	promptWhenLeaving = function (b) {
		// set the fn to be called
		// when leaving the page
		window.onbeforeunload = (b) ?
		function (msg) {
			saveChangesBtn.style.backgroundColor = "#479e8f";
			return function () {
				return msg;
			};
		}("You should save your changes before leaving.") :
		// returns undefined
		function (a) {
			saveChangesBtn.style.backgroundColor = "white";
			return a;
		}(undefined);
	},
	handleFileImport = function (event) {
		// stop at this point
		event.stopPropagation();
		event.preventDefault();
		// the files that were dropped
		var files = event.dataTransfer.files,
		numberOfFiles = files.length,
		reader = new FileReader(),
		file = files[0], // only read one file
		separator = !!function endsWith(str, suffix) {
			return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}(file.name, ".csv") ? "," : "\t";
		// ignore the drop if it was not a valid file
		if (separator !== "," && separator !== "\t") {
			// this is not a *.tsv or *.csv file
			console.log("Not valid file.");
			return;
		}
		// begin the read operation
		console.log(separator);
		reader.readAsText(file, "UTF-8");
		// init the reader event handlers
		reader.onload = function (event) {
			var csv = event.target.result,
			// split on lines and
			// wrap with HTML
			lines = csv.split(/\r\n|\n/).filter(function (line) {
				return line.length > 0;
			}).map(function (line, index) {
				return "<tr>" +
				(index === 0 ?
				line.split(separator).map(function (v, i) { return "<th><h6 contenteditable=\"true\">" + v + "</h6></th>"; }).join("") :
				line.split(separator).map(function (v, i) { return "<td contenteditable=\"true\">" + v + "</td>"; }).join("")) +
				"</tr>";
			}),
			// grab the elements
			head = document.querySelector("#data-table thead"),
			tbody = document.querySelector("#data-table tbody");
			// set the headers
			head.innerHTML = lines[0];
			tbody.innerHTML = lines.slice(1).join("");
			dropZone.style.display = "none";
			// refresh column count
			i = $("#data-table th").size();
			// the dataset needs saving
			promptWhenLeaving(true);
		};
	};
	window.addEventListener("DOMContentLoaded", function () {
		// the number of columns
		i = $("#data-table th").size();
		// init
		initHandlers();
		// when to show the drop zone
		document.getElementById("upload").addEventListener("click", function(event) {
			dropZone.style.display = "block";
		});
		// any key events in the data table could invalidate
		// all of the data
		dataDiv.addEventListener("keydown", function (e) {
			promptWhenLeaving(true);
		});
		// set up the drop zone,
		// even though it is hidden
		dropZone.addEventListener("dragover", function (event) {
			// allow us to drop
			event.preventDefault();
			return false;
		});
		dropZone.addEventListener("drop", handleFileImport);
		// by default the dataset is saved/current
		promptWhenLeaving(false);
		// prevent the user from downloading a dataset that
		// has not been saved yet
		downloadBtn.addEventListener("click", function (e) {
			if (window.onbeforeunload) {
				e.preventDefault();
				var msg = "Cannot download an unsaved dataset.";
				window.alert(msg);
			}
		});
		// if the introduction has not been dispayed yet
		// show the user a brief intro.js to the site
		if (window.localStorage.getItem("needsDataIntro") === "n") {
			return;
		}
		introJs().goToStep(2).start().oncomplete(function () {
			window.localStorage.setItem("needsDataIntro", "n");
		});
	});

// cache window and document
}(window, window.document);
