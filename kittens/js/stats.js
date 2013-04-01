!function (window, document, undefined) {

	var chartDiv = document.getElementById("chart"),
	statisticsDiv = document.getElementById("statistics"),
	row = document.getElementById("row"),
	chartSize = 684,
	r,
	createRaphael = function () {
		r = Raphael(chartDiv, chartSize, chartSize / 2);
		updateChart();
	},
	updateChart = function (e) {
		var index = row.value - 1; // one based indexing
		$.get("/api/dataset", {
			"uuid": statisticsDiv.dataset.uuid
		}).done(function (data) {
			console.log(data.rows[index].values);
			r.clear();
			r.piechart(
				chartSize / 2 / 2,
				chartSize / 2 / 2,
				150,
				data.rows[index].values.map(function (v) {
					return parseFloat(v);
				}),
				{ legend: data.headers, legendpos: "east" }
			).hover(function () {
				this.sector.stop();
				this.sector.scale(1.1, 1.1, this.cx, this.cy);
				if (this.label) {
					this.label[0].stop();
					this.label[0].attr({ r: 7.5 });
					this.label[1].attr({ "font-weight": 800 });
				}
			}, function () {
				this.sector.animate({ transform: 's1 1 ' + this.cx + ' ' + this.cy }, 500, "bounce");
				if (this.label) {
					this.label[0].animate({ r: 5 }, 500, "bounce");
					this.label[1].attr({ "font-weight": 400 });
				}
			});
		});
	};
	window.addEventListener("load", function(event) {
		createRaphael();
		row.addEventListener("change", updateChart);
	});

}(window, window.document);