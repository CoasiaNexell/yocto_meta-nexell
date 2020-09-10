inherit linux-nexell-base

DESCRIPTION = "nexell gl tools wrapper"

LICENSE = "CLOSED"

PV ?= "1.0+git${SRCPV}"
SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}-${PV}:"

SRC_PATH = "${BSP_VENDOR_DIR}/library/nx-gl-tools"

SRC_URI = "file://${SRC_PATH}"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit autotools

DEPENDS = "mesa libdrm nx-v4l2 nx-renderer nexell-drm-mali-qt"
PRIVATE_LIBS  += "libMali.so"

EXTRA_OECONF = " \
	'--prefix=${STAGING_DIR_HOST} AR_FLAGS="crU"' \
"

EXTRA_OEMAKE += " \
	'AM_CFLAGS=$(WARN_CFLAGS) -I./include -I${STAGING_INCDIR}' \
	'libnx_gl_tools_la_LDFLAGS=-L${STAGING_LIBDIR} -lEGL -ldrm -lnx_v4l2 -lnx_renderer -L./lib/linux/${ARCH_TYPE_NUM} -lnxgpusurf' \
"

LDFLAGS_append = " -lEGL -ldrm -lnx_v4l2 -lnx_renderer -L./lib/linux/${ARCH_TYPE_NUM} -lnxgpusurf"

do_myp() {
    rm -rf ${S}
    cp -a ${WORKDIR}/${SRC_PATH} ${S}
    rm -rf ${WORKDIR}/home
}
addtask myp before do_patch after do_unpack

do_configure() {
	cd ${S}
	NOCONFIGURE=true ./autogen.sh
	oe_runconf --enable-static
}

do_compile() {
	cd ${S}
	oe_runmake clean
	oe_runmake
}

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${includedir}

	cd ${S}
	oe_runmake install DESTDIR=${D}
	cp -apr ${D}/* ${BASE_WORKDIR}/extra-rootfs-support/
}

INSANE_SKIP_${PN} = "dev-so invalid-packageconfig file-rdeps"
FILES_${PN} = "${libdir} ${includedir}"
FILES_${PN}-dev = "${includedir}"

REPLACES_${PN} = "libwayland-egl.so"
