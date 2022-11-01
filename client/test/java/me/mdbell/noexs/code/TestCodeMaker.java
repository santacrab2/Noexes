package me.mdbell.noexs.code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class TestCodeMaker {

	static String sourceCodeDir = "test/resources/codemaker";
	static String resDump = "target/codemaker_res";

	@BeforeAll
	static void initAll() {
		try {
			FileUtils.forceMkdir(new File(resDump));
		} catch (IOException e) {
			fail("Failure during init", e);
		}
	}

	@TestFactory
	Collection<DynamicTest> testCodeMakerFromSources() {
		List<DynamicTest> tests = new ArrayList<>();
		Collection<File> files = FileUtils.listFiles(new File(sourceCodeDir), new String[] { "csf" }, false);
		tests.add(DynamicTest.dynamicTest("Check minimum files > 0 (" + tests.size() + ")",
				() -> assertTrue(tests.size() > 1)));
		for (File file : files) {
			tests.add(DynamicTest.dynamicTest("" + file.getName(), () -> testSourceCode(file)));
		}
		return tests;
	}

	void testSourceCode(File sourceCodeFile) {

		try {
			String sourceCode = FileUtils.readFileToString(sourceCodeFile, "UTF-8");
			String res = CheatCodeMaker.generateCodeFromString(sourceCode);

			String resFileName = FilenameUtils.getBaseName(sourceCodeFile.getPath());
			File resFile = new File(resDump, resFileName + ".txt");

			FileUtils.writeStringToFile(resFile, res, "UTF-8");

			File expectedFile = new File(sourceCodeDir, resFileName + ".txt");
			String expectedCode = FileUtils.readFileToString(expectedFile, "UTF-8");

			assertEquals(expectedCode, res, "Code generated must be the same");

		} catch (IOException e) {
			fail("Failure during test", e);
		}
	}
}
