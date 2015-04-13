var submitButton1 = $("input[value='Delete Coupon']");
    
function updateState()  {
    var numChecked = $("input[type='checkbox']:checked").length;
    submitButton1.attr("disabled", numChecked < 1);
}

