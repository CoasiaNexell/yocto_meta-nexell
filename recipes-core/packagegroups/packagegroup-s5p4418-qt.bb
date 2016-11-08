#
# Copyright (C) 2007 OpenedHand Ltd.
#
# derived from oe-core: meta/recipes-core/packagegroups/packagegroup-core-boot.bb

SUMMARY = "QT requirements"
DESCRIPTION = "The qt set of packages required by NEXELL S5p4418 AVN"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

#helloworld-nexell-module is only 32bit make support
NEXELL_EXAMPLES = " \
    helloworld-nexell \
    helloworld-nexell-module \
    example-libs \
    "

RDEPENDS_${PN} = " \
    nexell-drm-mali \
    testsuite-s5p4418 \
    rtl-8188eus-32 \
    ${NEXELL_EXAMPLES} \
    "