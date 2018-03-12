require qt5-nexell.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# fix compile error
SRC_URI += " \
    file://0001-Fix-Qt3D-build.patch \
"

SRC_URI[md5sum] = "95c1ffb98b014109d8f3a433e1130f03"
SRC_URI[sha256sum] = "8ab38bd7536603d90775ade350a6ca019fe7c50fc61ea360947f697f166e9a9a"
