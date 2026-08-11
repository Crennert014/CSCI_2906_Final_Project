"use strict";

(function () {
  var storageKey = "wf_theme";
  var root = document.documentElement;
  var validThemes = ["light", "dark", "dark-forest", "dark-ember"];

  function setTheme(theme) {
    if (validThemes.indexOf(theme) === -1) theme = "light";
    root.setAttribute("data-theme", theme);
    try {
      localStorage.setItem(storageKey, theme);
    } catch (e) {
      // Ignore storage failures in restricted environments.
    }
    syncControl(theme);
  }

  function syncControl(theme) {
    var sel = document.getElementById("themeToggle");
    if (sel) sel.value = theme;
  }

  function getPreferredTheme() {
    try {
      var saved = localStorage.getItem(storageKey);
      if (validThemes.indexOf(saved) !== -1) return saved;
    } catch (e) {
      // Fall back to system preference.
    }
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  }

  setTheme(getPreferredTheme());

  var control = document.getElementById("themeToggle");
  if (control) {
    control.addEventListener("change", function () {
      setTheme(this.value);
    });
  }
})();
