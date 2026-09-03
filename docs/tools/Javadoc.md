# Javadoc

> [!NOTE]
> The Javadoc are available on [GitHub Pages](https://ignfab.github.io/voxatile).

We use [Javadoc](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) Comments to document our code. If you're already familiar with this tool, skip to the [Usage](#Usage) section.

## What is a Javadoc Comment?

A Javadoc Comment is a special type of comment in Java, used to document methods, fields, types or packages. They are located right above the element they document, and look like regular multiline comments, with an additional asterisk at the beginning:
```java
/** Single-line Javadoc Comment */
public static final int MY_CONSTANT = 7;

/**
 * Multi-line
 * Javadoc Comment
 */
public void myMethod() {}
```

It is possible to generate a static HTML documentation from these comments. Many tools also show them when you hover the element in text editor.

### Structure and format

The comment is always structured the same: first, a description about the element, then, optional tags with special meanings. The first sentence of the description will be used as a summary when listing every element, thus it should be meaningful and punctuation must be respected (the sentence should end with a period follow by a blank).

Line breaks are ignored, but the description allows HTML formatting, especially `<p>` tags to create new paragraphs. The parser will handle unclosed tags, meaning that closing a paragraph is never needed (so avoid any unnecessary tags to keep the doc uncluttered).

HTML is also possible in text content within some tags, such as `@see ...`. Furthermore, some tags (known as inline tags) can be used to format description and text content, such as `{@code ...}`.

### Block tags

Block tags (opposed to inline tags) are semantic sections of the doc. Most of them are optional, but some may be required in some cases.

<details>
<summary>List of block tags</summary>

#### `@param`

Specifies the description of each parameter. Mandatory when the element has any parameter. There is one tag per parameter, and this also applies to type parameters and record components. The description should be clear and concise, and can span across multiple lines if needed. If passing `null` argument has a special meaning, it must be described here. Example:

```
@param index the index of the element, relative to {@link #offset}
@param label the label to display above the element,
             or {@code null} to display no label

@param <T> the type of elements in this list
```

#### `@return`

Explains the return value of the method. Mandatory when not `void`. If the method can return `null` is some case, it must be made explicit here.

#### `@throws`

Specifies the exceptions that may occur, and what could cause them to be thrown. Mandatory when the method throws any checked exception. There is one tag per exception thrown, and unchecked exception may or may not be documented, if relevant. Example:

```
@throws IOException if the request cannot be completed because of a network failure
@throws NullPointerException if {@code value} is {@code null}
```

#### `@deprecated`

Specifies the reason why this element is deprecated, and what to use instead. Mandatory when the element is annotated by the `@Deprecated` annotation.

#### `@see`

Adds a link about something interesting to see to get more information. This can be another element, or an external HTTP link (using HTML). Multiple tags can be specified, and an optional label can be added. Example:

```
@see #someMethod(int, String)
@see AnotherClass Link label
@see <a href="https://example.org">Example Link</a>
```

#### `@author`

Specifies the author of the code. Rarely used, optional.

#### `@version`

Specifies the version of the code. Rarely used, optional.

#### `@since`

Specifies the version when this element was added. Rarely used, optional.

</details>

### Inline tags

Inline tags can be used nearly anywhere in Javadoc to format texts.

<details>
<summary>List of inline tags</summary>

#### `{@link}` / `{@linkplain}`

Inserts a link similar to `@see` (but inline). `{@linkplain}` uses a text font instead of a code font (monospaced). Example:

```java
/**
 * Register a {@linkplain TextLabel text label} for this element.
 *
 * @deprecated Labels are now HTML. Use {@link #registerRichLabel(HtmlLabel)} instead.
 */
```

#### `{@literal}`

Inserts text without any formatting. May be useful to avoid HTML characters such as `<` or `>` from being interpreted.

#### `{@code}`

Inserts text with code font (monospaced). The same behaviour as `{@literal}` also applies.

#### `{@value}`

Inserts the value of a constant. Can be the current documented element or another one. Example:

```java
/** The maximum number of elements. (value = {@value}) */
public static final int MAX_SIZE = 512;

/**
 * Add an element. Be careful, you can only add up to {@value #MAX_SIZE} elements!
 */
```

#### `{@inheritDoc}`

Inserts the documentation from the parent element. It can be used to insert the whole parent documentation, or only some sections if used within a specific block tag. Rarely used, because the Javadoc tool automatically inherit doc when needed. Example:

```java
// Whole parent documentation
/** {@inheritDoc} */

// Only return description
/**
 * New description
 * @param x new param
 * @return {@inheritDoc}
 */
```

#### `{@docRoot}`

Inserts the path of the generated documentation file. May be useful to build link to relative file. Rarely used. Example:

```
@see <a href="{@docRoot}/technical-specifications.pdf">Technical specifications PDF file</a>
```

</details>

## Usage

We try to document as much code as possible. However, we prioritize the documentation of abstractions, public / reusable elements, and tools (`utils` package). We do not seek a 100% Javadoc coverage.

When adding elements that do meet the above criteria, or you think are worth documenting, you should write the documentation at the same time. Do not forget to check if the documentation of elements you modified is still correct!

To generate the static HTML documentation, we use the `maven-javadoc-plugin` to run the Javadoc tool using the [`javadoc:javadoc` Maven goal](Maven.md#generate-javadoc). A [GitHub workflow](GitHub-workflows.md#javadoc) also generates automatically Javadoc on push. Due to technical limitation on GitHub's side, it currently only [deploys on GitHub Pages](GitHub-workflows.md#github-pages) from the `main` branch, and upload the generated files as workflow artifact otherwise.
