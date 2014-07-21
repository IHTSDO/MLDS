"use strict";

describe("init plugin to test public method destroy", function() {

  beforeEach(function() {
    input = $("<input>");
    input.intlTelInput();
  });

  afterEach(function() {
    input = null;
  });

  it("adds the markup", function() {
    expect(getParentElement()).toHaveClass("intl-tel-input");
    expect(getSelectedFlagContainer()).toExist();
    expect(getListElement()).toExist();
  });
  
  it("binds the events listeners", function() {
    var listeners = $._data(input[0], 'events');
    // autoHideDialCode=true
    expect("blur" in listeners).toBeTruthy();
    expect("focus" in listeners).toBeTruthy();
    expect("mousedown" in listeners).toBeTruthy();
    // autoFormat=true
    expect("keypress" in listeners).toBeTruthy();
    // normal
    expect("keyup" in listeners).toBeTruthy();
  });


  describe("calling destroy", function() {
  
    beforeEach(function() {
      input.intlTelInput("destroy");
    });

    it("removes the markup", function() {
      expect(getParentElement()).not.toHaveClass("intl-tel-input");
      expect(getSelectedFlagContainer()).not.toExist();
      expect(getListElement()).not.toExist();
    });

    it("unbinds the event listeners", function() {
      var listeners = $._data(input[0], 'events');
      expect(listeners).toBeUndefined();
    });
  
  });

});