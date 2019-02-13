package org.geogebra.keyboard.base;

/**
 * The actions correspond to {@link ActionType#CUSTOM}.
 */
public enum Action {
	// TODO remove SWITCH_KEYBOARD after we move to new keyboard in Web
	BACKSPACE_DELETE,

	CAPS_LOCK,

	RETURN_ENTER,

	LEFT_CURSOR,

	RIGHT_CURSOR,

	NONE,

	TOGGLE_ACCENT_ACUTE,

	TOGGLE_ACCENT_GRAVE,

	TOGGLE_ACCENT_CARON,

	TOGGLE_ACCENT_CIRCUMFLEX,

	SWITCH_KEYBOARD,

	SWITCH_TO_ABC,

	SWITCH_TO_SPECIAL_SYMBOLS,

	SWITCH_TO_LATIN_CHARACTERS,

	SWITCH_TO_123,

	ANS
}