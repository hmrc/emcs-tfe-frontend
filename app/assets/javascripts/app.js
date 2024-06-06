// prevent resubmit warning
if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
  window.history.replaceState(null, null, window.location.href);
}

document.addEventListener('DOMContentLoaded', function(event) {

  // handle back click
  const backLink = document.querySelector('.govuk-back-link');
  if (backLink) {
    backLink.addEventListener('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      window.history.back();
    });
  }

  // handle print link visibility  click
  const printPageContainer = document.querySelector('.print-page');
  if (printPageContainer) {
    printPageContainer.classList.remove('govuk-!-display-none'); //Remove the hide class from the link (JS enabled = show, JS disabled = hide)
  }

  const printLink = document.getElementById('print-link');

  if (printLink) {
    printLink.addEventListener('click', function (e) {
      e.preventDefault();
      window.print();
    });
  }
});

