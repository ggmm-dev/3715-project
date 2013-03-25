!function (window, document, undefined) {

	// the input element to ensure that the user knows
	// what password they have entered
	var eConfirmPassword = function () {
		var e = document.createElement("input");
		e.type = "password";
		e.name = "confirm-password";
		e.placeholder = "Confirm password".toLowerCase();
		e.pattern = "^.{6,}$";
		e.required = true;
		return e;
	};
	var validate = function (event) {
		event.preventDefault();
		var form = event.target;
		if (form.password.value === form["confirm-password"].value) {
			event.target.submit();
		}
		return false;
	};
	window.addEventListener("DOMContentLoaded", function(event) {
		// add the confirm password btn to the form
		var registerForm = document.getElementById("register");
		var registerPass = document.querySelector("#register > input[type='password']");
		var registerConfirmPass = eConfirmPassword();
		registerForm.insertBefore(registerConfirmPass, registerPass.nextSibling);
		// ensure that the passwords match when submitting
		registerForm.addEventListener("submit", validate);
	});

}(window, window.document);
