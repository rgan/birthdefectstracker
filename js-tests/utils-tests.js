module("utils");

test("should format number", function(){
    expect(1);
    equals(formatNumber(72.88678453), "72.89", 'should round off to two digits')
});

test("should truncate if too long", function() {
    expect(2);
    var str = '';
    for(i=0; i< MAX_FIELD_SIZE_FOR_REPORTS; i++) {
        str += 'a';
    }
    equals(truncateIfTooLong(str + 'aaa'), str + '...', 'should truncate if long')
    equals(truncateIfTooLong(str), str, 'should not truncate if less than allowed')
});

test("should return today", function() {
   expect(1)
   var d = new Date()
   var expectedDate = d.getFullYear() + '-' + d.getMonth() + '-' + d.getDate();
   equals(today(), expectedDate, 'should return date today as yyyy-mm-dd')
});