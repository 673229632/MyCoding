/**
	 * PDF上传
	 * 
	 * @param response
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/pdfPortrait", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pdfUpload(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "imgFile", required = false) MultipartFile multipartFile) {
		LoggerUtils.info(logger, "upload PDF上传处理开始！");
		try {
			// 获取文件路径
			String filePath = FileUtil.getPortraitPath(request);
			String path = "";
			
			if (multipartFile != null) {
				//判断文件类型是否是mp4格式
				if (!FileUtil.checkSuffix(multipartFile.getOriginalFilename(), PDF_ALLOWED_EXTENSION)) {
					return BaseReturn.response(MsgCode.FAILURE);
				}
				InputStream in=multipartFile.getInputStream();
				FileInputStream fin=null;
				// 如果是FileInputStream类型，进行转换
				if (in instanceof FileInputStream) {
				    fin = (FileInputStream) in;
				} else { // 否则，转型失败
				    throw new Exception();
				}
				String type=FileHeaderUtil.getFileType(fin);
				if (!PDF_TYPE.equals(type)) {
					return BaseReturn.response(MsgCode.FAILURE,1);
				}
				//获取文件上传路径
				path = filePath + FileUtil.encodingFilename(multipartFile.getOriginalFilename());
				String relativePath = FileUtil.getRootPath(request) + path;
				// 存入硬盘
				multipartFile.transferTo(new File(relativePath));
				// 路径返回
				LoggerUtils.info(logger, "upload PDF上传处理结束！");
				return FileUtil.successResponse(request, multipartFile.getOriginalFilename(), path);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("upload PDF上传失败", ex);
			return BaseReturn.response(MsgCode.FAILURE);
		}
		return BaseReturn.response(MsgCode.FAILURE);
	}


/**
     * 获取头像目录，若不存在则直接创建一个
     *
     * @param userID 用户ID
     * @return
     */
    public static String getPortraitPath(HttpServletRequest request) {
        String realPath = defaultBaseDir+"/" + datePath() + "/";
        File file = new File(getRootPath(request) +realPath);
        //判断文件夹是否存在，不存在则创建一个
        if (!file.exists() || !file.isDirectory()) {
            if (!file.mkdirs()) {
                try {
                    throw new Exception("创建头像文件夹失败！");
                } catch (Exception e) {
                    LoggerUtils.error(logger, "创建头像文件夹失败！", e);
                    e.printStackTrace();
                }
            }
        }
        return realPath;
    }



/**
     * 文件名编码
     * 
     * @param filename
     * @return
     */
    public static String encodingFilename(String filename) {
        filename = filename.replace("_", " ");
        filename = System.nanoTime() + filename;
        return filename;
    }



/**
     * 获得服务器根路径
     * 
     * @param request
     * @return 服务器根路径
     */
    public static String getRootPath(HttpServletRequest request) {
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        if (StringUtils.isEmpty(rootPath)) {
            try {
                rootPath = request.getSession().getServletContext().getResource("/").getPath();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return rootPath;
    }
