### Nexell - For Yocto build with using local source

EXTERNALSRC = "${BSP_VENDOR_DIR}/firmware/optee/optee_build"
EXTERNALSRC_BUILD = "${EXTERNALSRC}"

S = "${WORKDIR}/${EXTERNALSRC}"

SRC_URI = "file://${EXTERNALSRC}"
SRC_URI_append = " file://0001-optee-build-customize-for-yocto.patch"

do_patch() {
    :
}

do_mypatch() {
    cd ${S}
    if [ -e PATCH_DONE_BY_YOCTO ];then
        ${_PATCH_FILE_REVERT_BY_GEN_}
        rm PATCH_DONE_BY_YOCTO
    fi
    ${_PATCH_FILE_BY_GEN_}
    touch PATCH_DONE_BY_YOCTO
}
addtask mypatch before do_compile after do_configure



_PATCH_FILE_BY_GEN_="patch -p1 < ${WORKDIR}/0001-optee-build-customize-for-yocto.patch -f;"
_PATCH_FILE_REVERT_BY_GEN_="patch -R -p1 < ${WORKDIR}/0001-optee-build-customize-for-yocto.patch -f;"
LOCAL_KERNEL_SOURCE_USING="true"

PATH_ATF := "${BSP_VENDOR_DIR}/firmware/arm-trusted-firmware"
PATH_BL1 := "${BSP_VENDOR_DIR}/bl1/bl1-s5p6818"
PATH_L-LOADER := "${BSP_VENDOR_DIR}/firmware/l-loader"
PATH_OPTEE_BUILD := "${BSP_VENDOR_DIR}/firmware/optee/optee_build"
PATH_OPTEE_CLIENT := "${BSP_VENDOR_DIR}/firmware/optee/optee_client"
PATH_OPTEE_LINUXDRIVER := "${BSP_VENDOR_DIR}/firmware/optee/optee_linuxdriver"
PATH_OPTEE_OS := "${BSP_VENDOR_DIR}/firmware/optee/optee_os"
PATH_OPTEE_TEST := "${BSP_VENDOR_DIR}/firmware/optee/optee_test"
PATH_U-BOOT := "${BSP_VENDOR_DIR}/u-boot/u-boot-2016.01"

PATH_KERNEL_SRC := "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"