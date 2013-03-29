var Dataset = (function (window, document, undefined) {

	"use strict";
	var constructor = function (name, description) {
		// private
		var _uuid = name,
		    _name = description,
		    _desc = description,
		    _headers = [],
		    _rows = [];
		// public
		this.getName = function () {
			return _name;
		};
		this.setName = function (name) {
			_name = name;
			return this;
		};
		this.setHeaders = function (headers) {
			_headers = headers;
			return this;
		};
		this.addRows = function (rows) {
			_rows.push(rows);
			return this;
		};
		this.toString = function () {
			return JSON.stringify({
				"uuid": _uuid,
				"name": _name,
				"description": description,
				"headers": _headers,
				"rows": _rows
			});
		};
	};
	return constructor;

})(window, window.document);
