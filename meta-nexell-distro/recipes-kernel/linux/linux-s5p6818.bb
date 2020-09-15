DESCRIPTION ?= "Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.4.83"

PV = "${LINUX_VERSION}+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_PATH = "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"
SRC_URI = "file://${SRC_PATH}"
SRC_URI_append = " file://0001-Yocto-unaccess-changed.patch "

S = "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"
B = "${WORKDIR}/${BP}"

EXTERNALSRC_pn-linux-s5p6818 = "${S}"
EXTERNALSRC_BUILD_pn-linux-s5p6818 = "${B}"

KCONFIG_MODE="--alldefconfig"
ARM_ARCH = "arm64"

DEPENDS += "optee-build"

export KBUILD_OUTPUT = "${B}"
OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"
LINUX_VERSION_EXTENSION ?= "-s5p6818"

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

_PATCH_FILE_BY_GEN_="patch -p1 < ${WORKDIR}/0001-Yocto-unaccess-changed.patch -f;"
_PATCH_FILE_REVERT_BY_GEN_="patch -R -p1 < ${WORKDIR}/0001-Yocto-unaccess-changed.patch -f;"

do_configure_prepend () {
    if [ "${EXTERNALSRC}" != "${EXTERNALSRC_BUILD}" ]; then
    oe_runmake -C ${S} distclean
    else
        file=${B}/.kernel_defconfig
    if [ -e ${file} ] && [ -d ${WORKDIR} ]; then
        conf=$(cat ${file})
        if [ "${conf}" != "${KBUILD_DEFCONFIG}" ]; then
        rm ${file}; echo ${KBUILD_DEFCONFIG} >> ${file};
            oe_runmake -C ${S} distclean
        fi
    else
        echo ${KBUILD_DEFCONFIG} >> ${file};
        oe_runmake -C ${S} distclean
    fi
    fi

    if [ ! -f ${B}/.config ]; then
    oe_runmake ARCH=arm64 -C ${S} O=${B} ${KBUILD_DEFCONFIG}
    fi
}

do_clean_distclean () {
    if [ "${EXTERNALSRC}" = "${EXTERNALSRC_BUILD}" ]; then
    oe_runmake -C ${S} distclean
    fi
}
addtask do_clean_distclean before do_cleansstate after do_clean

do_kernel_configme() {
	echo "skip this options"
}


# make boot.img
inherit nexell-mkimage
do_deploy_append() {
    make_bootimg ${D}/${KERNEL_IMAGEDEST} ${DEPLOY_DIR_IMAGE}
}


