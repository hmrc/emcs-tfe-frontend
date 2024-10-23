/*
Note - updated to work with the HMRC Frontend implementation [https://github.com/hmrc/play-frontend-hmrc/tree/v8.5.0?tab=readme-ov-file#adding-accessible-autocomplete-to-a-select-input]
in order to provide:-
 - Trimming of leading spaces on input due to an issue on Mac OS voice control [https://github.com/alphagov/accessible-autocomplete/issues/494]
 - If a user selects an EPC code from the list (for example "B000: Beer") and then deletes that and types another EPC code
   which is not in the list (for example "1234: Fred"), when they submit the form, the first EPC code (B000: Beer) will be
   submitted, not the answer which is showing in the input when they hit submit.
 - An autocomplete bug due to the error message being associated with the hidden select input. The bug doesn't announce this
   error message when the user lands on the input field.
 */

if (typeof HMRCAccessibleAutocomplete != 'undefined' && document.querySelector('[data-module="hmrc-accessible-autocomplete"]') != null) {
    var originalSelect = document.querySelector('[data-module="hmrc-accessible-autocomplete"]');
    // load autocomplete - now handled by the HMRC component wrapper in Twirl
    // accessibleAutocomplete.enhanceSelectElement({
    //     selectElement: originalSelect,
    //     showAllValues: true
    // });

    // =====================================================
    // Polyfill autocomplete once loaded
    // =====================================================
    var checkForLoad = setInterval(checkForAutocompleteLoad, 50);
    var parentForm = upTo(originalSelect, 'form');

    function polyfillAutocomplete(){
        var combo = parentForm.querySelector('[role="combobox"]');

        // =====================================================
        // Update autocomplete once loaded with fallback's aria attributes
        // Ensures hint and error are read out before usage instructions
        // =====================================================
        if(originalSelect && originalSelect.getAttribute('aria-describedby') > ""){
            if(parentForm){
                if(combo){
                    combo.setAttribute('aria-describedby', originalSelect.getAttribute('aria-describedby') + ' ' + combo.getAttribute('aria-describedby'));
                }
            }
        }
        // =====================================================
        // Update autocomplete once loaded with error styling if needed
        // This won't work if the autocomplete css is loaded after the frontend library css because
        // the autocomplete's border will override the error class's border (they are both the same specificity)
        // but we can use the class assigned to build a more specific rule
        // =====================================================
        setErrorClass();
        function setErrorClass(){
            if(originalSelect && originalSelect.classList.contains("govuk-select--error")){
                if(parentForm){
                    if(combo){
                        combo.classList.add("govuk-input--error");
                        // Also set up an event listener to check for changes to input so we know when to repeat the copy
                        combo.addEventListener('focus', function(){setErrorClass()});
                        combo.addEventListener('blur', function(){setErrorClass()});
                        combo.addEventListener('change', function(){setErrorClass()});
                    }
                }
            }
        }

        // =====================================================
        // Ensure when user replaces valid answer with a non-valid answer, then valid answer is not retained
        // =====================================================
        var holdSubmit = true;
        parentForm.addEventListener('submit', function(e){
            if(holdSubmit){
                e.preventDefault()
                if(originalSelect.querySelectorAll('[selected]').length > 0 || originalSelect.value > ""){

                    var resetSelect = false;

                    if(originalSelect.value){
                        if(combo.value != originalSelect.querySelector('option[value="' + originalSelect.value +'"]').text){
                            resetSelect = true;
                        }
                    }
                    if(resetSelect){
                        originalSelect.value = "";
                        if(originalSelect.querySelectorAll('[selected]').length > 0){
                            originalSelect.querySelectorAll('[selected]')[0].removeAttribute('selected');
                        }
                    }
                }

                holdSubmit = false;
                //parentForm.submit();
                HTMLFormElement.prototype.submit.call(parentForm); // because submit buttons have id of "submit" which masks the form's natural form.submit() function
            }
        })

        // Additional 'input' event listener which trims leading whitespace characters [https://github.com/alphagov/accessible-autocomplete/issues/494]
        parentForm.addEventListener('input', function(event) {
            var userInput = event.target.value;
            var trimmed = userInput.replace(/^\s+/g, '');
            event.target.value = trimmed;
        });

    }
    function checkForAutocompleteLoad(){
        if(parentForm.querySelector('[role="combobox"]')){
            clearInterval(checkForLoad)
            polyfillAutocomplete();
        }
    }


}


// Find first ancestor of el with tagName
// or undefined if not found
function upTo(el, tagName) {
    tagName = tagName.toLowerCase();

    while (el && el.parentNode) {
        el = el.parentNode;
        if (el.tagName && el.tagName.toLowerCase() == tagName) {
            return el;
        }
    }

    // Many DOM methods return null if they don't
    // find the element they are searching for
    // It would be OK to omit the following and just
    // return undefined
    return null;
}
