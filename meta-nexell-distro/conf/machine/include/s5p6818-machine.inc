# RAM Size : "512mb" , "1g", "2g"
NEXELL_MEM_SIZE ?= "2g"

require conf/machine/include/arm/arch-arm64.inc
include nexell-base.inc
include nexell-tools.inc
include nexell-mem-${NEXELL_MEM_SIZE}.inc

NEXELL_KERNEL_VER ?= "4"
NEXELL_DEV_PORTNUM ?= "2"
NEXELL_SYSLOG ?= "n"


# 32bit machine = 32/arm, 64bit machine = 64/arm64
ARM_ARCH = "arm64"
ARCH_TYPE_NUM = "64"
ARCH_TYPE = "arm64"

# ------------------------------------------------------------
# Nexell 2nd Boot Configuration
# ------------------------------------------------------------
# BL1
BL1_BUILD_CONFIG='BOARD="${NEXELL_BL1_BOARD_NAME}" KERNEL_VER="${NEXELL_KERNEL_VER}" SYSLOG="${NEXELL_SYSLOG}" DEVICE_PORT="${NEXELL_DEV_PORTNUM}" '
BL1_BIN='bl1-${NEXELL_BL1_IMAGE_NAME}.bin'
BL1_FINAL_BIN ?= 'bl1-${BSP_TARGET_BOARD_PREFIX}.bin'
BL1_EMMCBOOT ?= 'bl1-emmcboot.bin'
BL1_SECURE_EMMCBOOT ?= 'bl1-emmcboot.img'

# ------------------------------------------------------------
# Nexell U-Boot Configuration
# ------------------------------------------------------------
UBOOT_DEFCONFIG ?= "s5p6818_${NEXELL_BOARD_NAME}_defconfig"
UBOOT_BIN ?= 'u-boot.bin'

# ------------------------------------------------------------
# Nexell USB image Configuration
# ------------------------------------------------------------
FIP_NONSECURE_USB_BIN ?= 'fip-nonsecure-usb.bin'
FIP_LOADER_USB_IMG ?= 'fip-loader-usb.img'

# ------------------------------------------------------------
# Nexell Kernel Configuration
# ------------------------------------------------------------
KBUILD_DEFCONFIG ?= "s5p6818_${NEXELL_BOARD_NAME}_defconfig"
NEXELL_KERNEL_IMAGE_NAME ?= "Image"
# boot partition size : 64MB
NEXELL_BOOT_PARTITION_SIZE ?= "67108864"

# ------------------------------------------------------------
# Nexell optee Configuration, only s5p6818
# ------------------------------------------------------------
OPTEE_PLAT_DRAM_SIZE ?= "2048"
OPTEE_PLAT_UART_BASE ?= "0xC00A3000"
OPTEE_BUILD_TARGET_SOCNAME ?= "${NEXELL_BOARD_SOCNAME}"
OPTEE_BUILD_TARGET_MACHINE ?= "${NEXELL_BOARD_SOCNAME}-${BSP_TARGET_BOARD_NAME}"
IMAGE_INSTALL_append = " optee-build"


NEXELL_EMMC_EXTRA_OPTS ?='-m 0x60200 -b ${NEXELL_DEVID_SDMMC} -p ${NEXELL_PORT_EMMC} -m 0x1E0200 -b ${NEXELL_DEVID_SDMMC} -p ${NEXELL_PORT_EMMC}'
NEXELL_SD_EXTRA_OPTS ?='-m 0x60200 -b ${NEXELL_DEVID_SDMMC} -p ${NEXELL_PORT_SD} -m 0x1E0200 -b ${NEXELL_DEVID_SDMMC} -p ${NEXELL_PORT_SD}'
# NEXELL_USB_EXTRA_OPTS is defined in optee-build_git.bb file, because of it need to calculate size of image.
#NEXELL_USB_EXTRA_OPTS ?='-u -m ${MEM_SECURE_LOAD_ADDR} -z ${FIP_SEC_SIZE} -m ${MEM_SECURE_LOAD_ADDR} -z ${FIP_NONSEC_SIZE}'

# ------------------------------------------------------------
# Nexell Environment Image Configuration
# ------------------------------------------------------------
NEXELL_ENV_PARTITION_SIZE ?= "16384"
NEXELL_ENV_BIN ?= "params.bin"
NEXELL_DEFAULT_ENVS ?= "default_envs.txt"


