var checkBoxes = $("input[type='checkbox']"),
    toggleButton = $("input[value='Select All']");

checkBoxes.click(function() {
    updateState();
    var numChecked = $("input[type='checkbox']:checked").length;
    if (numChecked == 0) {
        $("input[value='Clear All']").prop("value", "Select All");
    }
    else if (numChecked == 1) {
        $("input[value='Select All']").prop("value", "Clear All");
    }
});

toggleButton.click(function() {
    updateState();
});

function toggle() {
    var numChecked = $("input[type='checkbox']:checked").length;
    if (numChecked == 0) {
        $("input[type='checkbox']").prop("checked", true);
        $("input[value='Select All']").prop("value", "Clear All");
    }
    else {
        $("input[type='checkbox']").prop("checked", false);
        $("input[value='Clear All']").prop("value", "Select All");
    }
}

function clearState() {
    var arr = document.getElementsByClassName("css-checkbox");    
    for (index=0;index<arr.length;index++) {        
        arr[index].checked=false;
    }
}
