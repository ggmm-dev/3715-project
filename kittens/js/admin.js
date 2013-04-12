!function (window, document, undefined) {

	"use strict";

	var
	deleteUsersBtn = document.getElementById("delete-users"),
	deleteDatasetsBtn = document.getElementById("delete-datasets"),
	makeAdminBtn = document.getElementById("make-admin"),
	usersList = document.getElementById("users-list"),
	datasetsList = document.getElementById("datasets-list");

	window.addEventListener("DOMContentLoaded", function () {
		// delete users
		deleteUsersBtn.addEventListener("click", function (e) {
			console.clear();
			var uuids = [];
			$(usersList).find(":checked").parent().each(function (i, e) {
				uuids.push($(e).siblings()[0].dataset.uuid);
			});
			$.ajax("/api/user", {
				type: "DELETE",
				data: JSON.stringify(uuids)
			}).done(function (data) {
				console.log(data);
				$.each(data, function (i, e) {
					$("[data-uuid=\"" + e + "\"]").parent().remove();
				});
			});
		});
		// make users administrators
		makeAdminBtn.addEventListener("click", function () {
			console.clear();
			var uuids = [];
			$(usersList).find(":checked").parent().each(function (i, e) {
				uuids.push($(e).siblings()[0].dataset.uuid);
			});
			$.ajax("/api/user", {
				type: "PUT",
				data: JSON.stringify(uuids)
			}).done(function (data) {
				console.log(data);
				$.each(data, function (i, e) {
					$("[data-uuid=\"" + e + "\"]").parent().remove();
				});
			});
		});
		// delete datasets
		deleteDatasetsBtn.addEventListener("click", function () {
			console.clear();
			var uuids = [];
			$(datasetsList).find(":checked").parent().each(function (i, e) {
				uuids.push($(e).siblings()[0].dataset.uuid);
			});
			$.ajax("/api/dataset", {
				type: "DELETE",
				data: JSON.stringify(uuids)
			}).done(function (data) {
				console.log(data);
				$.each(data, function (i, e) {
					$("[data-uuid=\"" + e + "\"]").parent().remove();
				});
			});
		});
	});

// cache window and document
}(window, window.document);
