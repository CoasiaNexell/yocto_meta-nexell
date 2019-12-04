#
# Copyright (C) 2007 OpenedHand Ltd.
#
# derived from oe-core: meta/recipes-core/packagegroups/packagegroup-core-boot.bb

SUMMARY = "TINY UI requirements"
DESCRIPTION = "The qt set of packages required by NEXELL S5P4418 NAVI"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

RDEPENDS_${PN} = " \
    nexell-drm-mali \
    testsuite-s5p4418 \
    "