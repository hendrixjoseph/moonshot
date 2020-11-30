rem $env:Path += ";C:\Program Files\Inkscape\bin"

inkscape --export-filename=../android/res/drawable-hdpi/ic_launcher.png -C --export-width=72 icon.svg
inkscape --export-filename=../android/res/drawable-mdpi/ic_launcher.png -C --export-width=48 icon.svg
inkscape --export-filename=../android/res/drawable-xhdpi/ic_launcher.png -C --export-width=96 icon.svg
inkscape --export-filename=../android/res/drawable-xxhdpi/ic_launcher.png -C --export-width=144 icon.svg
inkscape --export-filename=../android/res/drawable-xxxhdpi/ic_launcher.png -C --export-width=192 icon.svg
inkscape --export-filename=../android/ic_launcher-web.png -C --export-width=512 icon.svg