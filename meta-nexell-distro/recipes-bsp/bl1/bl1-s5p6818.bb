require bl1-secure.inc

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://README;md5=754608f69d5791d96a0a96281ae48814"
PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

SRC_URI = "git://github.com/CoasiaNexell/bl1_s5p6818;protocol=https;branch=nexell"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "arm-eabi-4.8"
TOOLCHAIN_ARCH32_EABI = "${RECIPE_SYSROOT}${datadir}/arm-eabi-4.8-toolchain/bin/"

EXTRA_OEMAKE = "\
    'VPATH=${WORKDIR}/git' \
"

do_compile () {
    oe_runmake CROSS_COMPILE_TOP=${TOOLCHAIN_ARCH32_EABI} ${BL1_BUILD_CONFIG} ${SECURE-BL1} -j 1
}

inherit deploy
inherit nexell-mkimage

do_deploy () {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${BL1_BIN} ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${S}/out/${BL1_EMMCBOOT} ${DEPLOY_DIR_IMAGE}

    # 1:${soc_name} |  2:${in_img} | 3:${out_img} | 4:${aes_key} | 5:${hash_name}
    make_2ndboot_image ${NEXELL_BOARD_SOCNAME} \
        ${DEPLOY_DIR_IMAGE}/${BL1_EMMCBOOT} \
        ${DEPLOY_DIR_IMAGE}/${BL1_SECURE_EMMCBOOT} \
        ${NEXELL_PRIVATE_KEY} \
        ""
}

addtask deploy after do_install
