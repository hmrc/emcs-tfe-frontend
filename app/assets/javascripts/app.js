// prevent resubmit warning
if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
  window.history.replaceState(null, null, window.location.href);
}

document.addEventListener('DOMContentLoaded', function(event) {

  // handle back click
  var backLink = document.querySelector('.govuk-back-link');
  if (backLink !== null) {
    backLink.addEventListener('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      window.history.back();
    });
  }
});

document.addEventListener('DOMContentLoaded', function(event) {

  // handle back click
  var printPageLink = document.querySelector('#print-page');
  if (printPageLink !== null) {
    printPageLink.addEventListener('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      window.print();
    });
  }
});

function printLink() {
  const printLink = document.getElementById('print-link');

  if (printLink != null && printLink != 'undefined') {
    printLink.addEventListener('click', function (e) {
      e.preventDefault();
      window.print();
    });
  }
}
document.addEventListener('DOMContentLoaded', function (event){
  printLink();
});
