LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://pyrope_bl2.lds;md5=c81ae199c8ac9bc25a4389a9c6b387b6"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/firmware/bl2-s5p4418"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS = "arm-eabi-4.8"
TOOLCHAIN_ARCH32_EABI = "${RECIPE_SYSROOT}${datadir}/arm-eabi-4.8-toolchain/bin/"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_compile () {
    cd ${S}
    oe_runmake CROSS_COMPILE_TOP=${TOOLCHAIN_ARCH32_EABI} QUICKBOOT=${QUICKBOOT_ENABLE} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${BL2_BIN} ${DEPLOY_DIR_IMAGE}

    # make secure image
    # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${load_addr} | 5:${jump_addr} | 6:${extra_opts}
    make_3rdboot_image ${NEXELL_BOARD_SOCNAME} \
            ${S}/out/${BL2_BIN} \
            ${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT} \
            ${BL2_EMMC_LOAD_ADDR} \
            ${BL2_EMMC_JUMP_ADDR} \
            '${BL2_EXTRA_OPTS}'

    # make fip loader image for usb
    make_fip_loader_usb_image
}

addtask deploy after do_install
