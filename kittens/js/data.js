!function (window, document, undefined) {

	"use strict";
	var i = 0,
	saveChangesBtn = document.getElementById("save"),
	addRowBtn = document.getElementById("add-row"),
	rmRowBtn = document.getElementById("rm-row"),
	addColBtn = document.getElementById("add-col"),
	rmColBtn = document.getElementById("rm-col"),
	dataTable = document.getElementById("data-table"),
	addRow = function (e) {
		console.log(e.target);
		var newRow = dataTable.insertRow(-1),
		j = 0,
		cell;
		for (j = 0; j < i; j++) {
			cell = newRow.insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode("33"));
		}
	},
	rmRow = function (e) {
		console.log(e.target);
		if (dataTable.rows.length <= 2) {
			return;
		}
		dataTable.deleteRow(-1);
	},
	addCol = function (e) {
		console.log(e.target);
		var rowCount = dataTable.rows.length,
		cell,
		h6,
		th,
		j = 0;
		for (j = 0; j < rowCount; j++) {
			if (j == 0) {
				// the table header
				th = document.createElement("th");
				h6 = document.createElement("h6");
				h6.contentEditable = true;
				h6.innerText = "New_column";
				th.appendChild(h6);
				dataTable.rows[j].appendChild(th);
				continue;
			}
			cell = dataTable.rows[j].insertCell(-1);
			cell.contentEditable = true;
			cell.appendChild(document.createTextNode("33"));
		}
		i = $("#data-table th").size();
	},
	rmCol = function (e) {
		console.log(e.target);
	},
	saveChanges = function (e) {
		console.log(e.target);
		var uuid = document.getElementById("data").dataset.uuid,
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
		});
	},
	initHandlers = function () {
		addRowBtn.addEventListener("click", addRow);
		rmRowBtn.addEventListener("click", rmRow);
		addColBtn.addEventListener("click", addCol);
		rmColBtn.addEventListener("click", rmCol);
		saveChangesBtn.addEventListener("click", saveChanges);
	};
	window.addEventListener("DOMContentLoaded", function () {
		i = $("#data-table th").size();
		initHandlers();
	});

}(window, window.document);