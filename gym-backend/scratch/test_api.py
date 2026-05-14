import requests
import time

try:
    start = time.time()
    r = requests.get('http://localhost:8081/api/publicaciones', timeout=5)
    end = time.time()
    print(f"Status: {r.status_code}")
    print(f"Time: {end - start}s")
    print(f"Body: {r.text[:200]}")
except Exception as e:
    print(f"Error: {e}")
