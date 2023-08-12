#!/bin/bash

# Count the number of 'assert' statements in the test files.
assert_count=$(grep -r "assert" src/test/ | wc -l)

# Count the number of test methods.
test_count=$(grep -r "@Test" src/test/ | wc -l)

# Calculate percentage coverage as the ratio of asserts to test methods.
coverage=$(( (assert_count * 100) / test_count ))

# Print the coverage.
echo "Coverage: ${coverage}%"

# Check against the threshold.
if [ "$coverage" -lt 70 ]; then
  echo "Coverage is below the threshold of 70%"
  exit 1
fi