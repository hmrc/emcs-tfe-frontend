

document.addEventListener('DOMContentLoaded', function(event) {

  var sortByButton = document.querySelector('#sortBySubmit');
  if (sortByButton !== null) {
    sortByButton.remove()
  }

  var sortBySelect = document.querySelector('#sortBy');
  if (sortBySelect !== null) {
    sortBySelect.addEventListener('change', function(e){
      e.preventDefault();
      e.stopPropagation();
      document.forms[0].submit();
    });
  }
});
