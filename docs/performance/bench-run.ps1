<#
Example PowerShell benchmark runner. Requires `wrk` or replace with your chosen tool.

Edit `$url`, `$duration`, `$connections`, `$threads` as needed.
#>
param()

$url = 'http://localhost:8080/api/v1/products'
$duration = '30s'
$connections = 100
$threads = 4
$out = "bench-$(Get-Date -Format yyyy-MM-dd_HH-mm-ss).txt"

Write-Host "Running benchmark against $url for $duration with $connections connections..."

# Example using wrk (must be installed and available in PATH)
try {
    & wrk -t $threads -c $connections -d $duration $url | Tee-Object $out
    Write-Host "Saved raw output to $out"
} catch {
    Write-Host "wrk not found or failed. Please install wrk or modify this script to use another tool."
}
