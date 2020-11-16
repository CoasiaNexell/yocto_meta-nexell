### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/firmware/optee/optee_linuxdriver"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"

LOCAL_KERNEL_SOURCE_USING="true"

PATH_ATF := "${BSP_VENDOR_DIR}/firmware/arm-trusted-firmware"
PATH_BL1 := "${BSP_VENDOR_DIR}/bl1/bl1-s5p6818"
PATH_L-LOADER := "${BSP_VENDOR_DIR}/firmware/l-loader"
PATH_OPTEE_BUILD := "${BSP_VENDOR_DIR}/optee/optee_build"
PATH_OPTEE_CLIENT := "${BSP_VENDOR_DIR}/firmware/optee/optee_client"
PATH_OPTEE_LINUXDRIVER := "${BSP_VENDOR_DIR}/firmware/optee/optee_linuxdriver"
PATH_OPTEE_OS := "${BSP_VENDOR_DIR}/firmware/optee/optee_os"
PATH_OPTEE_TEST := "${BSP_VENDOR_DIR}/optee/optee_test"
PATH_U-BOOT := "${BSP_VENDOR_DIR}/u-boot/u-boot-2016.01"
