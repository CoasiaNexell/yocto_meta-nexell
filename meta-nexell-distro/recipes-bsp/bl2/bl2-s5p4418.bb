LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://pyrope_bl2.lds;md5=c81ae199c8ac9bc25a4389a9c6b387b6"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/bl2_s5p4418;protocol=https;branch=nexell"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
DEPENDS = "arm-eabi-4.8"
TOOLCHAIN_ARCH32_EABI = "${RECIPE_SYSROOT}${datadir}/arm-eabi-4.8-toolchain/bin/"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

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

    # make fip image
    # 1:${in_img} |  2:${out_img} | 3:${seek_val} | 4:${bs_val}
    make_fip_image ${DEPLOY_DIR_IMAGE}/${BL2_EMMCBOOT} \
        ${DEPLOY_DIR_IMAGE}/${FIP_NONSECURE_USB_BIN} \
        "0" \
        "1"
}

addtask deploy after do_install
