

document.addEventListener('DOMContentLoaded', function(event) {

  var sortByButton = document.querySelector('#sortBySubmit');
  if (sortByButton) {
    sortByButton.remove()
  }

  var sortBySelect = document.querySelector('#sortBy');
  if (sortBySelect) {
    sortBySelect.addEventListener('change', function(){
      document.forms[0].submit();
    });
  }
});
