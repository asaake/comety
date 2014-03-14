Mocha.reporters.TAP_RESULT = function TAP_RESULT(runner, result) {
	
	if (!result) {
		result = {};
		mocha.tapResult = result;
	}
	
	Mocha.reporters.Base.call(this, runner);

	var n = 1;
	var passes = 0;
	var failures = 0;
	result.logs = [];
	result.isStart = false;
	result.isEnd = false;

	runner.on('start', function() {
		var total = runner.grepTotal(runner.suite);
		result.logs.push(sprintf('%d..%d', 1, total));
		result.isStart = true;
	});

	runner.on('test end', function() {
		++n;
	});

	runner.on('pending', function(test) {
		result.logs.push(sprintf('ok %d %s # SKIP -', n, title(test)));
	});

	runner.on('pass', function(test) {
		passes++;
		result.logs.push(sprintf('ok %d %s', n, title(test)));
	});

	runner.on('fail', function(test, err) {
		failures++;
		result.logs.push(sprintf('not ok %d %s', n, title(test)));
		if (err.stack)
			result.logs.push(sprintf(err.stack.replace(/^/gm, '  ')));
	});

	runner.on('end', function() {
		result.logs.push(sprintf('# tests ' + (passes + failures)));
		result.logs.push(sprintf('# pass ' + passes));
		result.logs.push(sprintf('# fail ' + failures));
		
		result.tests = passes + failures;
		result.passes = passes;
		result.failures = failures;
		
		result.isEnd = true;
	});
	
	function title(test) {
		return test.fullTitle().replace(/#/g, '');
	};
};