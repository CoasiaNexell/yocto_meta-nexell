### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/apps/nx_vip_test"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"
