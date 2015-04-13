var submitButton1 = $("input[value='Compare']");
    submitButton2 = $("input[value='Remove from Favorites']");
    
function updateState()  {
    var numChecked = $("input[type='checkbox']:checked").length;
    submitButton1.attr("disabled", numChecked < 2);
    submitButton2.attr("disabled", numChecked < 1);
}

function showDiv() {
}
