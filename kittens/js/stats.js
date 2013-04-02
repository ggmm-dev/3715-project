!function (window, document, undefined) {

	"use strict";

	var
	chartDiv      = document.getElementById("chart"),
	statisticsDiv = document.getElementById("statistics"),
	row           = document.getElementById("row"),
	chartSize     = 684,
	r,

	errorMsg = function (msg) {
		if (msg === false) {
			$("span.error").remove();
			return;
		}
		// create and append a error msg
		var span = document.createElement("span");
		span.style.color = "red";
		span.style.paddingLeft = "15px";
		span.textContent = msg;
		span.classList.add("error");
		$(statisticsDiv).append(span);
	},
	createRaphael = function () {
		r = new Raphael(chartDiv, chartSize, chartSize / 2);
		// draw the chart
		updateChart();
	},
	updateChart = function (e) {
		var index = row.value - 1; // one based indexing
		$.get("/api/dataset", {
			"uuid": statisticsDiv.dataset.uuid
		}).done(function (data) {
			// only numeric data can be charted
			if (data.rows[index].values.filter(function (v, i) {
				return !isNaN(v);
			}).length === 0) {
				errorMsg("Only numeric rows are allowed.");
				return;
			}
			// remove any existing errors
			errorMsg(false);
			// clear previous charts
			r.clear();
			r.piechart(
				// width
				chartSize / 2 / 2,
				// height
				chartSize / 2 / 2,
				// radius
				150,
				// values
				data.rows[index].values.map(function (v) {
					return parseFloat(v);
				}),
				// options
				{ legend: data.headers, legendpos: "east" }
			).hover(function () {
				// animate
				this.sector.stop();
				this.sector.scale(1.1, 1.1, this.cx, this.cy);
				if (this.label) {
					this.label[0].stop();
					this.label[0].attr({ r: 7.5 });
					this.label[1].attr({ "font-weight": 800 });
				}
			}, function () {
				// animate
				this.sector.animate({ transform: 's1 1 ' + this.cx + ' ' + this.cy }, 500, "bounce");
				if (this.label) {
					this.label[0].animate({ r: 5 }, 500, "bounce");
					this.label[1].attr({ "font-weight": 400 });
				}
			});
		});
	},
	showNewDataset = function (e) {
		$.get("/api/dataset", {
			"uuid": e.target.dataset.uuid
		}).done(function (data) {
			// update the heading
			$(statisticsDiv).find("h2").text(data.name);
			// fix the UUID
			statisticsDiv.dataset.uuid = data.UUID;
			// and update the row options
			row.innerHTML = data.rows.map(function (v, i) {
				return "<option value=\"" + (i + 1) + "\">" + (i + 1) + "</option>";
			}).join("");
			// redraw the chart
			updateChart();
		});
	};
	window.addEventListener("DOMContentLoaded", function () {
		// hide the create new project button
		document.getElementById("new-project").style.visibility = "hidden";
	});
	window.addEventListener("load", function(event) {
		// draw a chart
		createRaphael();
		// show projects
		$("aside > menu > ul > li > a").each(function (i, e) {
			e.addEventListener("click", showNewDataset);
		});
		// update the chart on new rows
		row.addEventListener("change", updateChart);
		// if needed, show a introduction
		if (window.localStorage.getItem("needsStatsIntro") === "n") {
			return;
		}
		introJs().goToStep(2).start().oncomplete(function () {
			window.localStorage.setItem("needsStatsIntro", "n");
		});
	});

// cache window and document
}(window, window.document);
