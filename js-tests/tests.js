load('js-tests/env-js/env.rhino.js');

window.onload = function(){
    print("Loading source files..");
    load('war/js/utils.js');
    print("Loading testrunner and tests");
    load('js-tests/testrunner.js');
    load('js-tests/utils-tests.js');
}

window.location = 'js-tests/birthdefect_tests.html';