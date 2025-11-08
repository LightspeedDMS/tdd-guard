/**
 * Java/Kotlin-specific TDD rules.
 *
 * ONLY applied to .java and .kt files. Go/Rust/Python/JavaScript/TypeScript unaffected.
 *
 * Key difference from other languages:
 * - Allows complete POJO/data class setup (fields + constructor + getters) as ONE change
 * - Based on Kent Beck's "Obvious Implementation" strategy
 * - Community consensus: POJOs/data classes are structural code, not behavioral
 *
 * This prompt is ONLY included when languageCategory === 'compiled' (Java and Kotlin).
 */
export const COMPILED_LANGUAGE_RULES = `## Java/Kotlin-Specific TDD Rules

### Research-Backed TDD Principles for Compiled Languages

Based on Kent Beck's "Test-Driven Development By Example" and Java TDD community consensus:

**Key Principle**: "If you know what to type, and you can do it quickly, then do it" - Kent Beck

### Understanding Compiled Language TDD

Compiled languages have an additional phase in the TDD cycle:

**RED Phase**: Test → **Compilation Failure** → Stub → **Test Failure** → Implementation → **Test Pass**

**Critical**: Compilation failures ARE legitimate test failures in TDD.

### CRITICAL DECISION TREE FOR JAVA/COMPILED LANGUAGES

When evaluating an Edit/Write operation:

**STEP 1**: Does a test file exist for this implementation?
- NO → BLOCK (premature implementation)
- YES → Continue to Step 2

**STEP 2**: Is this a POJO/Data Class (pure structure, no behavior)?
- Check: Are ALL changes just fields + constructor + getters/setters?
- Check: Is there ANY business logic, validation, calculations, conditionals?
- **Pure POJO (no logic)** → ALLOW complete structure (Kent Beck's "Obvious Implementation")
- **Has logic** → Continue to Step 3

**STEP 3**: Does test output show specific failure requiring this code?
- YES → ALLOW if implementation matches failure
- NO → BLOCK (need to run tests first)

### POJOs and Data Classes - Special Treatment

**Community Consensus**: Simple data classes (Java POJOs, Kotlin data classes, DTOs, Value Objects) with only fields, constructors, and trivial accessors are considered STRUCTURAL CODE, not behavioral code.

**Kent Beck's Guidance**: When implementation is obvious and you can type it quickly, use "Obvious Implementation" strategy rather than forcing incremental steps.

**For Data Classes**: Allow complete structural setup (fields + constructor + getters/setters) as ONE change when:
- Test file exists (shows test-first intent)
- No business logic anywhere
- Pure data structure (getters return fields, setters assign fields, constructor assigns params)
- This applies to Java POJOs AND Kotlin data classes
- EVEN when adding multiple properties/fields at once

### Compilation Phase Stubs

When a corresponding test file EXISTS, ALLOW these minimal stubs to fix compilation:

**CRITICAL**: Empty stubs are ALWAYS allowed when test file exists, EVEN if test output exists from other tests. The compilation phase requires structural stubs before tests can run.

#### Always Allowed (Compilation Stubs):
1. **Empty class/struct/interface**
   - \`public class Customer {}\`
   - \`struct User {}\`
   - \`interface IService {}\`

2. **Constructors with field assignments ONLY**
   - \`public Customer(String id) { this.id = id; }\`
   - Field declaration + constructor parameter assignment
   - NO business logic, calculations, or conditionals

3. **Simple Getters** (single field return)
   - \`public String getId() { return id; }\`
   - \`public int getCount() { return count; }\`
   - ONLY \`return fieldName;\` - nothing else

4. **Simple Setters** (single field assignment)
   - \`public void setId(String id) { this.id = id; }\`
   - ONLY \`this.field = parameter;\` - nothing else

5. **Field Declarations** (needed for constructors/getters/setters)
   - \`private String customerId;\`
   - \`private final int count;\`

6. **Empty Method Stubs**
   - \`public void process() {}\`
   - \`public String calculate() { return null; }\`
   - \`public int compute() { return 0; }\`

#### What DISQUALIFIES a class from being a "Pure POJO"?

ANY of these mean it's NOT a pure data class and needs test-driven incremental development:

- **Business logic**: if/else, switch, loops, conditionals, ternary operators
- **Calculations**: arithmetic operations, string manipulation, transformations
- **Method calls**: calling methods on other objects or static utilities (except simple assignments)
- **Validation**: null checks, throwing exceptions, data validation
- **Default values**: \`this.active = true;\`, \`this.id = UUID.randomUUID();\`
- **Object creation**: \`new Date()\`, \`LocalDate.now()\`, \`new ArrayList<>()\` with logic
- **Complex constructors**: Validation, transformation, or multi-step initialization

**❌ BLOCKED Examples (NOT Pure POJOs):**
// Default value initialization - NOT a POJO
public Customer(String id) {
    this.id = id;
    this.active = true;  // ❌ BLOCKED - adds default behavior
    this.createdAt = LocalDate.now();  // ❌ BLOCKED - logic
}

// Validation logic - NOT a POJO
public void setEmail(String email) {
    if (email == null) throw new IllegalArgumentException();  // ❌ BLOCKED
    this.email = email;
}

// Calculation - NOT a POJO
public int getTotal() {
    return price * quantity;  // ❌ BLOCKED - calculation
}

// Derived/computed field - NOT a POJO
public String getFullName() {
    return firstName + " " + lastName;  // ❌ BLOCKED - string manipulation
}

**Key Rule**: If it does ANYTHING other than store/retrieve data, it's NOT a POJO and requires test-driven development.

### Allowed Cohesive Structural Patterns

**CRITICAL**: These patterns are ALWAYS allowed for compiled languages when test file exists, regardless of whether test output exists. The compilation phase of TDD requires cohesive structural changes.

When test file exists (with or without test output), these COHESIVE patterns are ALLOWED:

#### Pattern 1: POJO/Data Class Setup (Kent Beck's "Obvious Implementation")
**ALLOW complete data class structure as ONE cohesive change** when:
- Test file exists (test-first development confirmed)
- Changes are purely structural (fields + constructor + simple accessors)
- Constructor params match fields exactly
- Getters ONLY return fields: return fieldName;
- Setters ONLY assign fields: this.field = value;
- **ZERO business logic, validation, calculations, or conditionals**

**Rationale**:
- Kent Beck: "If you know what to type and can do it quickly, then do it"
- Community consensus: POJOs are structural, not behavioral - covered by integration tests
- Data classes are "obvious implementation" - not requiring incremental TDD steps

**✅ ALLOWED - Complete Java POJO in ONE Edit:**
private String email;
private String name;
private int age;

public Customer(String id, String email, String name, int age) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.age = age;
}

public String getEmail() { return email; }
public String getName() { return name; }
public int getAge() { return age; }

**✅ ALLOWED - Complete Kotlin Data Class in ONE Edit:**
data class Customer(
    val id: String,
    val email: String,
    val name: String,
    val age: Int
)

**This is STRUCTURAL code** - pure data holders with no behavior.

#### Pattern 2: Annotations/Decorators (Non-Behavioral)
ALLOW adding annotations that don't change behavior:
- \`@Override\` on methods
- \`@NotNull\`, \`@Valid\`, validation annotations
- \`@JsonProperty\`, serialization annotations
- \`@Deprecated\`, documentation annotations

**✅ ALLOWED:**
\`\`\`java
@Override
public String toString() { return name; }

@NotNull
private String email;

@JsonProperty("customer_id")
public String getId() { return id; }
\`\`\`

#### Pattern 3: Type Changes (Structural Refactoring)
ALLOW changing types when purely structural:
- Primitive to wrapper: \`int → Integer\`
- Generic to specific: \`List → ArrayList\`
- String to value object: \`String → EmailAddress\`
- Adding generics: \`List → List<Customer>\`

**✅ ALLOWED:**
\`\`\`java
// Changing from primitive to wrapper
private Integer age;  // was: private int age;
\`\`\`

### When Test Output Exists

Follow standard TDD rules - implementation must match the specific test failure shown in output.

### Key Principle

The compilation phase requires STRUCTURAL stubs (classes, methods, fields) to make the test compile.
The test execution phase requires BEHAVIORAL implementation to make the test pass.

**Compilation stubs = Structure only (cohesive changes allowed)**
**Test pass implementation = Behavior only (incremental only)**

### Examples

**✅ ALLOWED - Compilation Phase Stub:**
\`\`\`java
public class Order {
    private String orderId;
    private BigDecimal total;

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
\`\`\`

**❌ BLOCKED - Business Logic:**
\`\`\`java
public class Order {
    private BigDecimal total;

    public void addItem(BigDecimal price) {
        if (total == null) {
            total = price;
        } else {
            total = total.add(price);  // Business logic!
        }
    }
}
\`\`\`

**Key Difference**: The first example is pure STRUCTURE. The second has LOGIC (if/else, calculations).
`
