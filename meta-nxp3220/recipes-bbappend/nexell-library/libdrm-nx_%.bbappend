### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_ROOT_DIR}/library/libdrm"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"
EXTERNALSRC_SYMLINKS = ""

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"