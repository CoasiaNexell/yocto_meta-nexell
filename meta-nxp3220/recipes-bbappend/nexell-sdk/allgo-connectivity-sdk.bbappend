### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/solutions/allgo-connectivity-sdk"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"

# Replace 'MACHINE' to support s5pxx18 recipes
export TARGET_MACHINE = "${BSP_TARGET_MACHINE}"
