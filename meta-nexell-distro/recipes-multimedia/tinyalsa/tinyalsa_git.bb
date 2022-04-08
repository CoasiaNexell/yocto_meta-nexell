SUMMARY = "tinyalsa"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.md;md5=7ea7f00d1daac577e75f5e833f5a27d6"

#SRCREV = "04fa39f287336c19953939f9523e04d0074a7f29"
SRC_URI = "git://github.com/tinyalsa/tinyalsa;protocol=https;tag=v1.0.2 \
           file://0001-tinyalsa-v1.0.2-make-build-error-fixed.patch"

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_compile() {
    oe_runmake clean
    oe_runmake CROSS_COMPILE=${TARGET_PREFIX} CPPFLAGS="${CFLAGS}" LDFLAGS+=" --sysroot=${STAGING_DIR_HOST} " CC="${CC}"
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP_${PN} = "installed-vs-shipped staticdev dev-so libdir ldflags"
INSANE_SKIP_${PN}-dbg += "libdir"
FILES_${PN} += "usr/local/bin usr/local/include/tinyalsa usr/local/lib usr/local/share/man/man1"
