# Performance benchmarking

This folder contains templates and instructions for running benchmarks and storing results.

Files:
- `bench-run.ps1`: example PowerShell script to run `wrk`/`ab` or similar and save JSON results.
- `benchmarks-template.json`: schema for storing run results.

Example (Windows PowerShell):

1. Start the application locally (default port 8080).

2. Run `bench-run.ps1` and follow the prompts or edit variables at top of the script.

Store results in this folder and add a short markdown summary per run.
