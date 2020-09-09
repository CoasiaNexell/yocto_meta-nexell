# RAM Size : "512mb" , "1g", "2g"
NEXELL_MEM_SIZE ?= "2g"

require conf/machine/include/tune-cortexa9.inc
include nexell-base.inc
include nexell-tools.inc
include nexell-mem-${NEXELL_MEM_SIZE}.inc

# 32bit machine = 32/arm, 64bit machine = 64/arm64
ARM_ARCH = "arm"
ARCH_TYPE_NUM = "32"
ARCH_TYPE = "arm32"

NEXELL_KERNEL_VER ?= "4"
NEXELL_ARM_SECURE ?= "n"
NEXELL_CHIPNAME ?= "nxp4330"
NEXELL_DEV_PORTNUM ?= "0"
NEXELL_SYSLOG ?= "n"

# ------------------------------------------------------------
# Nexell BL1 Configuration
# ------------------------------------------------------------
# BL1
BL1_BUILD_CONFIG='BOARD="${NEXELL_BL1_BOARD_NAME}" KERNEL_VER="${NEXELL_KERNEL_VER}" ARM_SECURE="${NEXELL_ARM_SECURE}" CHIPNAME="${NEXELL_CHIPNAME}" DEVICE_PORT="${NEXELL_DEV_PORTNUM}" SYSLOG="${NEXELL_SYSLOG}"'
#BL1_BIN='bl1-${NEXELL_BL1_IMAGE_NAME}.bin'
#BL1_FINAL_BIN='bl1-${NEXELL_BL1_FINAL_IMAGE_NAME}.bin'
#BL1_BUILD_CONFIG ?= 'BOARD="${NEXELL_BOARD_NAME}" KERNEL_VER="${NEXELL_KERNEL_VER}" ARM_SECURE="${NEXELL_ARM_SECURE}" CHIPNAME="${NEXELL_CHIPNAME}" DEVICE_PORT="${NEXELL_DEV_PORTNUM}" SYSLOG="${NEXELL_SYSLOG}"'
BL1_BIN ?= 'bl1-${NEXELL_BOARD_NAME}.bin'
BL1_FINAL_BIN ?= 'bl1-${BSP_TARGET_BOARD_PREFIX}.bin'
BL1_EMMCBOOT ?= 'bl1-emmcboot.bin'
BL1_SECURE_EMMCBOOT ?= 'bl1-emmcboot.img'

# ------------------------------------------------------------
# Nexell BL2 Configuration
# ------------------------------------------------------------
BL2_BIN ?= 'pyrope-bl2.bin'
BL2_EMMCBOOT ?= 'loader-emmc.img'
BL2_EMMC_LOAD_ADDR ?= "0xb0fe0000"
BL2_EMMC_JUMP_ADDR ?= "0xb0fe0400"
BL2_EXTRA_OPTS ?= '-m 0x40200 -b 3 -p ${NEXELL_DEV_PORTNUM} -m 0x1E0200 -b 3 -p ${NEXELL_DEV_PORTNUM} -m 0x60200 -b 3 -p ${NEXELL_DEV_PORTNUM}'

# ------------------------------------------------------------
# Nexell Dispatcher Configuration
# ------------------------------------------------------------
DISPATCHER_BUILD_CONFIG='BOARD="${NEXELL_DISPATCHER_BOARD_NAME}"'
#DISPATCHER_BUILD_CONFIG ?= 'BOARD="${NEXELL_BOARD_NAME}"'
DISPATCHER_BIN = 'armv7_dispatcher.bin'
DISPATCHER_EMMCBOOT ?= 'bl_mon.img'
DISPATCHER_EMMC_LOAD_ADDR ?= "0xffff0200"
DISPATCHER_EMMC_JUMP_ADDR ?= "0xffff0200"
DISPATCHER_EXTRA_OPTS ?= '-m 0x40200 -b 3 -p ${NEXELL_DEV_PORTNUM} -m 0x1E0200 -b 3 -p ${NEXELL_DEV_PORTNUM} -m 0x60200 -b 3 -p ${NEXELL_DEV_PORTNUM}'
DISPATCHER_FIP_NONSECURE_USB_BIN_OFFSET='35840'

# ------------------------------------------------------------
# Nexell U-Boot Configuration
# ------------------------------------------------------------
UBOOT_DEFCONFIG ?= "s5p4418_${NEXELL_BOARD_NAME}_defconfig"
UBOOT_BIN ?= 'u-boot.bin'
UBOOT_EMMCBOOT ?= 'bootloader.img'
UBOOT_EMMC_LOAD_ADDR ?= "0x43c00000"
UBOOT_EMMC_JUMP_ADDR ?= "0x43c00000"
UBBOT_FIP_NONSECURE_USB_BIN_OFFSET ?= '64512'

# ------------------------------------------------------------
# Nexell USB image Configuration
# ------------------------------------------------------------
FIP_NONSECURE_USB_BIN ?= 'fip-nonsecure-usb.bin'
FIP_LOADER_USB_IMG ?= 'fip-loader-usb.img'

# ------------------------------------------------------------
# Nexell Kernel Configuration
# ------------------------------------------------------------
KBUILD_DEFCONFIG ?= "s5p4418_${NEXELL_BOARD_NAME}_defconfig"
NEXELL_KERNEL_IMAGE_NAME ?= "zImage"
# boot partition size : 32MB
NEXELL_BOOT_PARTITION_SIZE ?= "33554432"

# ------------------------------------------------------------
# NSIH header address
# ------------------------------------------------------------
NSIH_LOAD_START_ADDRESS ?= "63c00000"

# ------------------------------------------------------------
# Nexell Environment Image Configuration
# ------------------------------------------------------------
NEXELL_ENV_PARTITION_SIZE ?= "16384"
NEXELL_ENV_BIN ?= "params.bin"
NEXELL_DEFAULT_ENVS ?= "default_envs.txt"

