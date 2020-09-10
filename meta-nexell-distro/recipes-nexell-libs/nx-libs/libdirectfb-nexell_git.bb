DESCRIPTION = "libdirectfb nexell"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://Makefile.am;md5=1c29f522bb1f7e5686af40aff4032b50"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/library/libdirectfb-nexell"

SRC_URI = "file://${SRC_PATH}"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

PV ?= "1.0+git${SRCPV}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "(nxp3220)"

DEPENDS = "libdrm directfb"

inherit autotools pkgconfig

GFXDRIVERS_MODULEDIR = "${libdir}/directfb-1.7-7"

EXTRA_OECONF = " \
    --host=${HOST_SYS} \
    --target=${TARGET_SYS} \
    --prefix=${STAGING_INCDIR} \
    --includedir=${STAGING_INCDIR} \
    "

EXTRA_OEMAKE = " \
    'CFLAGS+=-I${STAGING_KERNEL_DIR}/include/uapi/drm' \
    'MODULEDIR=${GFXDRIVERS_MODULEDIR}' \
"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

# Fix build error when EXTERNALSRC equal EXTERNALSRC_BUILD
# Error : source directory already configured
do_configure_prepend() {
    if [ -f ${S}/config.status ]; then
	oe_runmake -C ${S} distclean
    fi
}

INSANE_SKIP_${PN} = "dev-so invalid-packageconfig"
FILES_${PN} = "${GFXDRIVERS_MODULEDIR}/* "
RDEPENDS_${PN} += "libdrm directfb"
ALLOW_EMPTY_${PN} = "1"
