DESCRIPTION ?= "Linux Kernel"
SECTION = "kernel"
LICENSE = "GPLv2"

inherit kernel
require recipes-kernel/linux/linux-yocto.inc

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

LINUX_VERSION ?= "4.4.83"

PV = "${LINUX_VERSION}+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/linux_kernel-4.4.x;protocol=https;branch=nexell;depth=5 \
		file://0001-Yocto-unaccess-changed.patch \
"

S = "${BSP_VENDOR_DIR}/kernel/kernel-4.4.x"
B = "${WORKDIR}/${BP}"

EXTERNALSRC_pn-linux-s5p4418 = "${S}"
EXTERNALSRC_BUILD_pn-linux-s5p4418 = "${B}"

KCONFIG_MODE="--alldefconfig"
ARM_ARCH = "arm"
DEPENDS += "u-boot-nexell bl1-s5p4418 bl2-s5p4418 dispatcher-s5p4418"

export KBUILD_OUTPUT = "${B}"
OE_TERMINAL_EXPORTS += "KBUILD_OUTPUT"
LINUX_VERSION_EXTENSION ?= "-s5p4418"


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
    oe_runmake ARCH=arm -C ${S} O=${B} ${KBUILD_DEFCONFIG}
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


