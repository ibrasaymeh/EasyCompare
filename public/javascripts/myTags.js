var submitButton1 = $("input[value='Compare']");
    submitButton2 = $("input[value='Add to Favorites']");
    submitButton3 = $("input[value='Delete']");
    
function updateState()  {
    showDiv();
    var numChecked = $("input[type='checkbox']:checked").length;
    submitButton1.attr("disabled", numChecked < 2);
    submitButton2.attr("disabled", numChecked < 1);
    submitButton3.attr("disabled", numChecked < 1);   
}

function showDiv() {
    document.getElementById("optionNote").style.display = "none";
}

$(document).ready(function() {
    $('#optionNote').tooltipster({
        content: $('<ul class=listForHint1><li><p>At least one checkbox must be selected to enable the following options:</p></li><ul class=listForHint2><li><p>Add to Favorites</p></li><li><p>Delete</p></li></ul><li><p>At least two checkboxes must be selected to enable the <i>Compare</i> option.</p></li></ul>'),
        theme: 'tooltipster-light',
        delay: 0,
        position: 'bottom',
        animation: 'grow',
        maxWidth: 390
    });
});
