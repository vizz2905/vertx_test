start cmd /c start_mmVerticle.bat
timeout /T 4

start cmd /c start_verticleInfoDump.bat

timeout /T 8
start cmd /c start_mmClientBusOnly.bat

timeout /T 2
start cmd /c start_mmClientBusOnly.bat

timeout /T 2
start cmd /c start_mmClientBusOnly.bat

timeout /T 2
start cmd /c start_mmClientBusOnly.bat

timeout /T 2
start cmd /c start_mmClientBusOnly.bat