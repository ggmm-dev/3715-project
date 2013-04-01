!function (window, document, undefined) {

	"use strict";
	var i = 0,
	createNewProject = document.getElementById("new-project"),
	projectsList = document.getElementById("projects-list"),
	saveChangesBtn = document.getElementById("save"),
	addRowBtn = document.getElementById("add-row"),
	rmRowBtn = document.getElementById("rm-row"),
	addColBtn = document.getElementById("add-col"),
	rmColBtn = document.getElementById("rm-col"),
	dataTable = document.getElementById("data-table"),
	downloadBtn = document.getElementById("download"),
	dropZone = document.getElementById("drop-zone"),
	dataDiv = document.getElementById("data"),
	updateRightSide = function (data) {
		var newRowCount = data.rows.length,
		headers,
		rows,
		head = document.querySelector("#data-table thead tr"),
		tbody = document.querySelector("#data-table tbody"),
		j = 0;
		// set the headers
		head.innerHTML = data.headers.map(function (v) {
			return "<th><h6 contenteditable=\"true\">" + v + "</h6></th>";
		}).join("");
		tbody.innerHTML = data.rows.map(function (r) {
			return "<tr>" + r.values.map(function (v) {
				return "<td contenteditable=\"true\">" + v + "</td>";
			}).join("") + "</tr>";
		}).join("");
		$("#data > h2").text(data.name);
		dataDiv.dataset.uuid = data.UUID;
		downloadBtn.href = "/download/dataset?uuid=" + data.UUID;
	},
	addRow = function (e) {
		console.log(e.target);
		var newRow = dataTable.insertRow(-1),
		j = 0,
		cell;
		for (j = 0; j < i; j++) {
			cell = newRow.insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode(""));
		}
		promptWhenLeaving(true);
	},
	rmRow = function (e) {
		console.log(e.target);
		if (dataTable.rows.length <= 2) {
			return;
		}
		dataTable.deleteRow(-1);
		promptWhenLeaving(true);
	},
	addCol = function (e) {
		console.log(e.target);
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
				h6.textContent = "NewColumn";
				th.appendChild(h6);
				dataTable.rows[j].appendChild(th);
				continue;
			}
			cell = dataTable.rows[j].insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode("33"));
		}
		i = $("#data-table th").size();
		promptWhenLeaving(true);
	},
	rmCol = function (e) {
		console.log(e.target);
		var rowCount = dataTable.rows.length,
		colCount = dataTable.rows[0].cells.length,
		j = 0;
		for (j = 0; j < rowCount; j++) {
			if (colCount <= 1) {
				return;
			}
			dataTable.rows[j].deleteCell(-1);
		}
		promptWhenLeaving(true);
	},
	saveChanges = function (e) {
		console.log(e.target);
		var uuid = dataDiv.dataset.uuid,
		headers = [],
		rows = [];
		$("#data-table thead h6").each(function () {
			headers.push(this.textContent);
		});
		$("#data-table tbody tr").each(function () {
			var cells = [];
			$(this).find("td").each(function () {
				cells.push(this.textContent);
			});
			rows.push(cells);
		});
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
			console.log(data);
			promptWhenLeaving(false);
		});
	},
	showProject = function (e) {
		console.log(e.target);
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			console.log(data);
			updateRightSide(data);
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
	initHandlers = function () {
		createNewProject.addEventListener("click", addNewProject);
		addRowBtn.addEventListener("click", addRow);
		rmRowBtn.addEventListener("click", rmRow);
		addColBtn.addEventListener("click", addCol);
		rmColBtn.addEventListener("click", rmCol);
		saveChangesBtn.addEventListener("click", saveChanges);
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showProject);
		});
	},
	promptWhenLeaving = function (b) {
		window.onbeforeunload = (b) ?
		function () {
			return "You should save your changes before leaving.";
		} :
		undefined;
	},
	handleFileImport = function (event) {
		event.stopPropagation();
		event.preventDefault();
		var files = event.dataTransfer.files,
		numberOfFiles = files.length,
		reader = new FileReader(),
		file = files[0], // only read one file
		separator = !!function endsWith(str, suffix) {
			return str.indexOf(suffix, str.length - suffix.length) !== -1;
		}(file.name, ".csv") ? "," : "\t";
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
			lines = csv.split(/\r\n|\n/).filter(function (line) {
				return line.length > 0;
			}).map(function (line, index) {
				return "<tr>" +
				(index === 0 ?
				line.split(separator).map(function (v, i) { return "<th><h6 contenteditable=\"true\">" + v + "</h6></th>"; }).join("") :
				line.split(separator).map(function (v, i) { return "<td contenteditable=\"true\">" + v + "</td>"; }).join("")) +
				"</tr>";
			}),
			head = document.querySelector("#data-table thead"),
			tbody = document.querySelector("#data-table tbody");
			// set the headers
			console.log(lines);
			head.innerHTML = lines[0];
			tbody.innerHTML = lines.slice(1).join("");
			dropZone.style.display = "none";
			promptWhenLeaving(true);
		};
	};
	window.addEventListener("DOMContentLoaded", function () {
		i = $("#data-table th").size();
		initHandlers();
		document.getElementById("upload").addEventListener("click", function(event) {
			console.log(event.target);
			dropZone.style.display = "block";
		});
		dataDiv.addEventListener("keyup", function (e) {
			promptWhenLeaving(true);
		});
		dropZone.addEventListener("dragover", function (event) {
			// allow us to drop
			event.preventDefault();
			return false;
		});
		dropZone.addEventListener("drop", handleFileImport);
		if (window.localStorage.getItem("needsDataIntro") === "n") {
			return;
		}
		introJs().goToStep(2).start().oncomplete(function () {
			window.localStorage.setItem("needsDataIntro", "n");
		});
	});

}(window, window.document);
