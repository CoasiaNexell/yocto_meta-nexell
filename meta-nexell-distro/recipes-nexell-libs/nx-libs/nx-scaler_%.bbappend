### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/library/nx-scaler"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"
EXTERNALSRC_SYMLINKS = ""

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"