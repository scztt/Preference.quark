Preference {
	classvar writePending=false;
	classvar dependants;

	var keys, <default;
	var <spec, <class;

	*new {
		|...keys|
		^super.newCopyArgs(keys).init
	}

	init {
		Archive.read();
	}

	default_{
		|value|
		default = this.prConstrainedValue(value);
		spec !? { spec.default = default };
	}

	hasValue {
		^(Archive.global.at(*keys).notNil)
	}

	prConstrainedValue {
		|value|
		if (value.isNil) { ^value };

		if (spec.notNil) {
			value = spec.constrain(value);
		};
		if (class.notNil and: { value.isKindOf(class).not }) {
			value = value.as(class)
		};
		^value;
	}

	prStoredValue {
		^Archive.global.at(*keys)
	}

	value {
		^this.prStoredValue() ?? default
	}

	value_{
		|value|
		value = this.prConstrainedValue(value);
		if (value != this.value) {
			Archive.global.putAtPath(keys, value);
			this.changed(\value, value);
		}
	}

	spec_{
		|newSpec|
		spec = newSpec;
		this.default = this.default ?? { spec !? { spec.default } };
		this.value = this.prStoredValue(); // re-constrain
	}

	class_{
		|newClass|
		class = newClass;
		this.default = this.default;
		this.value = this.prStoredValue(); // re-constrain
	}

	clear {
		this.value = nil;
	}

	write {
		if (writePending.not) {
			writePending = true;

			{
				writePending = false;
				Archive.write();
			}.defer(1);
		}
	}
}